package com.test.hello;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.unstream.mandelbrot.Fractal;
import net.unstream.mandelbrot.FractalRepository;
import net.unstream.mandelbrot.Image;
import net.unstream.mandelbrot.ImageRepository;
import net.unstream.mandelbrot.MandelbrotService;
import net.unstream.mandelbrot.MandelbrotServiceException;
import net.unstream.mandelbrot.User;
import net.unstream.mandelbrot.UserRepository;

import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class FractalController {
	
	@Inject
	MandelbrotService mService;
	
	@Autowired GraphDatabase graphDatabase;
	@Autowired FractalRepository fractalRepository;
	@Autowired ImageRepository imageRepository;
	@Autowired UserRepository userRepository;


	
	private final static Logger LOG = LoggerFactory.getLogger(FractalController.class);

    @RequestMapping("/")
    public String index() {
        return "redirect: /listfractals";
    }
    
    @RequestMapping("/listfractals")
    @Transactional(readOnly=true)
    public String listFractals(Model model, final HttpServletRequest request, String mode) {
    	model.addAttribute("page", "listfractals");
		injectUser(model);

		HttpSession session = request.getSession();
        
        if (mode == null) {
			mode = (String) session.getAttribute("mode");
        }
    	List<Fractal> fractals = new ArrayList<Fractal>();
    	Transaction tx = graphDatabase.beginTx();
    	try {
			Iterable<Fractal> iterable = fractalRepository.findAll();
			for (Fractal fractal : iterable) {
				fractals.add(fractal);
			}
			tx.success();
		} catch (Exception e) {
			throw new MandelbrotServiceException(e);
		} finally {
			tx.close();
		}
		model.addAttribute("fractals", fractals);

		if ("tiles".equals(mode)) {
			session.setAttribute("mode", "tiles");
	        return "listfractalstiles";
		} else {
			session.setAttribute("mode", "list");
			return "listfractals";
		}
    }
    
    private void injectUser(Model model) {
    	String user = "anonymous";
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if (!(authentication instanceof AnonymousAuthenticationToken)) {
    	    user = authentication.getName(); 
    	}
    	model.addAttribute("user", user);
    }

	@RequestMapping(value="/mandelbrot")
    public String mandelbrot(
    		Fractal fractal, final BindingResult bindingResult, final boolean reset,  
    		HttpServletRequest request,
    		Model model) {
    	model.addAttribute("page", "mandelbrot");
		injectUser(model);

		if (bindingResult.hasErrors()) {
			return "mandelbrot";
		}

		if (reset) {
    		fractal = new Fractal();
    	}
    	HttpSession session = request.getSession();
		if (fractal == null) {
			fractal = (Fractal) session.getAttribute("fractal");
			if (fractal == null) {
				fractal = new Fractal(); 
			}
		}

		session.setAttribute("fractal", fractal);
		final String thumbId = UUID.randomUUID().toString();
		final String imgId = UUID.randomUUID().toString();
		session.setAttribute(thumbId, mService.computeMandelBrotPng(fractal, 64, 64));
		session.setAttribute(imgId, mService.computeMandelBrotPng(fractal, 500, 500));
		model.addAttribute("fractal", fractal);
		model.addAttribute("imgId", imgId);
		model.addAttribute("thumbId", thumbId);
    	return "mandelbrot";
    }

    @RequestMapping(value = "/getMandelbrotImage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    @Transactional
    public byte[] getMandelbrotImage(String imgId, HttpServletRequest request) throws MandelbrotServiceException, InterruptedException, ExecutionException {
    	byte[] img = null;
    	Object idObject = request.getSession().getAttribute(imgId);
    	if (idObject != null) {
        	if (idObject instanceof Future<?>) {
        		Future<byte[]> imgFuture = (Future<byte[]>) idObject;
            	img = imgFuture.get();
        	}
    	} else {
    		//try to load it from the DB
    		try {
				long id = Long.parseLong(imgId);
				
			  	Transaction tx = graphDatabase.beginTx();
		    	try {
					img = imageRepository.findById(id).getImage();
					tx.success();
				} catch (Exception e) {
					throw new MandelbrotServiceException(e);
				} finally {
					tx.close();
				}
			} catch (NumberFormatException e) {
				throw new MandelbrotServiceException(e);
			}
    	}
    	return img;
    }

    /**
     * Retrieve an image from the database.
     * @param id image id
     * @return PNG image
     */
    @RequestMapping(value = "/image", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    @Transactional(readOnly=true)
    public byte[] image(long id) {
    	byte[] img = null;
    	Transaction tx = graphDatabase.beginTx();
    	try {
        	img = imageRepository.findById(id).getImage();
        	tx.success();
    	} catch (IllegalArgumentException e) {
    		tx.failure();
    	} finally {
    		tx.close();
    	} 
    	return img;
    }
    
    @RequestMapping("/mandelbrot/{id}")
	public String mandelbrotRead(@PathVariable Long id, Model model) {
    	Fractal fractal = null;
	  	Transaction tx = graphDatabase.beginTx();
    	try {
    		fractal = fractalRepository.findById(id);
			tx.success();
		} catch (Exception e) {
			throw new MandelbrotServiceException(e);
		} finally {
			tx.close();
		}
		model.addAttribute("fractal", fractal);
		model.addAttribute("imgId", fractal.getImage().getId());
    	model.addAttribute("page", "mandelbrot");
		injectUser(model);
		return "mandelbrot";
    }

    @RequestMapping("/mandelbrot/save")
    @Transactional
    public String mandelbrotSave(
		Fractal fractal, final String imgId, final String thumbId, 
		HttpServletRequest request,
		Model model) throws ExecutionException, InterruptedException {
    	HttpSession session = request.getSession();

    	Transaction tx = graphDatabase.beginTx();
    	try {
        	if (fractal.getId() == null) {
            	Image thumb = new Image();
        		thumb.setHeight(64);
        		thumb.setWidth(64);
        		thumb.setImage(((Future<byte[]>) session.getAttribute(thumbId)).get());
        		
            	Image full = new Image();
        		full.setImage(((Future<byte[]>) session.getAttribute(imgId)).get());

        		fractal.setThumbnail(thumb);
        		fractal.setImage(full);
        	} else {
        		Fractal savedFractal = fractalRepository.findById(fractal.getId());
        		fractal.setImage(savedFractal.getImage());
        		fractal.setThumbnail(savedFractal.getThumbnail());
        	}


    		
    		fractalRepository.save(fractal);
        	tx.success();
    	} catch (IllegalArgumentException e) {
    		tx.failure();
    	} finally {
    		tx.close();
    	}
    	model.addAttribute("page", "mandelbrot");
		injectUser(model);
		model.addAttribute("fractal", fractal);
		model.addAttribute("imgId", imgId);
		model.addAttribute("thumbId", thumbId);
		return "mandelbrot";
    }

    @RequestMapping(value="/mandelbrot/{id}", method=RequestMethod.DELETE, //produces = MediaType.APPLICATION_JSON_VALUE,
    		consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Transactional
    public void mandelbrotDelete(@PathVariable Long id) {
    	Transaction tx = graphDatabase.beginTx();
    	try {
        	fractalRepository.delete(id);
        	tx.success();
    	} catch (IllegalArgumentException e) {
    		tx.failure();
    	} finally {
    		tx.close();
    	} 
    }
    
//    @RequestMapping(value = "/colorMapImage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
//    @ResponseBody
//    public byte[] colorMapImage(String color1, String color2, String color3, HttpServletRequest request) throws MandelbrotServiceException, InterruptedException, ExecutionException {
//    	byte[] img = mService.computeColorGradientPng(Color.decode(color1), Color.decode(color2), Color.decode(color3));
//    	return img;
//
//    }
//    
    @RequestMapping("/about")
    public String about(Model model) {
    	model.addAttribute("page", "about");
		injectUser(model);
        return "about";
    }

    @RequestMapping("/login")
    public String login(Model model) {
    	model.addAttribute("page", "login");
		injectUser(model);
        return "login";
    }

    @RequestMapping("/signup")
    public String signup(User user, Model model) {
    	model.addAttribute("page", "login");
		injectUser(model);
    	
    	Transaction tx = graphDatabase.beginTx();
    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    	user.setPassword(encoder.encode(user.getPassword()));
    	try {
    		userRepository.save(user);
        	tx.success();
    	} catch (IllegalArgumentException e) {
    		tx.failure();
    	} finally {
    		tx.close();
    	}
        return "redirect:mandelbrot";
    }

    
}
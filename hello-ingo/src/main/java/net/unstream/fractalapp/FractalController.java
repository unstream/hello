package net.unstream.fractalapp;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.unstream.fractal.api.FractalService;
import net.unstream.fractal.api.MandelbrotService;
import net.unstream.fractal.api.MandelbrotServiceException;
import net.unstream.fractal.api.UserNotFoundException;
import net.unstream.fractal.api.UserService;
import net.unstream.fractal.api.domain.Fractal;
import net.unstream.fractal.api.domain.Image;
import net.unstream.fractal.api.domain.User;
import net.unstream.fractalapp.security.CustomAuthenticationProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	
//	@Lazy @Autowired private FractalRepository fractalRepository;
//	@Autowired private  GraphDatabase graphDatabase;
//	@Autowired private ImageRepository imageRepository;

	@Autowired private FractalService fractalService;
	@Autowired private UserService userService;

	@Autowired private CustomAuthenticationProvider customAuthenticationProvider;



	
	private final static Logger LOG = LoggerFactory.getLogger(FractalController.class);

    @RequestMapping("/listfractals")
    @Transactional(readOnly=true)
    public String listFractals(Model model, final HttpServletRequest request, String mode) {
		HttpSession session = request.getSession();
        
        if (mode == null) {
			mode = (String) session.getAttribute("mode");
        }
    	List<Fractal> fractals = fractalService.findAll();
		model.addAttribute("fractals", fractals);

		if ("tiles".equals(mode)) {
			session.setAttribute("mode", "tiles");
	        return "listfractalstiles";
		} else {
			session.setAttribute("mode", "list");
			return "listfractals";
		}
    }
    
	@RequestMapping(value={"/", "/mandelbrot"})
    public String mandelbrot(
    		Fractal fractal, final BindingResult bindingResult, final boolean reset,  
    		HttpServletRequest request,
    		Model model) {

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
		/* detach the object after submit */
		fractal.setId(null);
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
				img = fractalService.findImageById(id).getImage();
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
		img = fractalService.findImageById(id).getImage();
    	return img;
    }

    /**
     * Retrieve an image from the database.
     * @param id image id
     * @return PNG image
     */
    @RequestMapping(value = "/bigimage.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] bigImage(long id) {
    	byte[] img = null;
    	Fractal fractal = null;
		fractal = fractalService.findById(id);
    	Future<byte[]> imgFuture = mService.computeMandelBrotPng(fractal, 1500, 1500);
    	try {
			img = imgFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.error(e.getMessage(), e);
		}
		return img;
    }
    
    @RequestMapping("/mandelbrot/{id}")
	public String mandelbrotRead(@PathVariable Long id, Model model) {
    	Fractal fractal = null;
   		fractal = fractalService.findById(id);
		model.addAttribute("fractal", fractal);
		model.addAttribute("imgId", fractal.getImage().getId());
		return "mandelbrot";
    }

    @RequestMapping("/mandelbrot/save")
    public String mandelbrotSave(
		Fractal fractal, final String imgId, final String thumbId, 
		HttpServletRequest request,
		Model model, Principal principal) throws ExecutionException, InterruptedException, UserNotFoundException {
    	HttpSession session = request.getSession();
    	if (fractal.getId() == null) {
        	Image thumb = new Image();
    		thumb.setHeight(64);
    		thumb.setWidth(64);
    		thumb.setImage(((Future<byte[]>) session.getAttribute(thumbId)).get());
        	Image full = new Image();
    		full.setImage(((Future<byte[]>) session.getAttribute(imgId)).get());

    		fractal.setThumbnail(thumb);
    		fractal.setImage(full);
    		User user = userService.findByUsername(principal.getName());
    		fractal.setCreator(user);
    		
    	} else {
    		Fractal savedFractal = fractalService.findById(fractal.getId());
    		fractal.setImage(savedFractal.getImage());
    		fractal.setThumbnail(savedFractal.getThumbnail());
    	}
    	fractalService.save(fractal);
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
       	fractalService.delete(id);
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
        return "about";
    }


}
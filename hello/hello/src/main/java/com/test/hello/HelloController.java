package com.test.hello;

import java.awt.Color;
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
import net.unstream.mandelbrot.MandelbrotService;
import net.unstream.mandelbrot.MandelbrotServiceException;

import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.hello.domain.User;

@Controller
public class HelloController {
	
	@Inject
	MandelbrotService mService;
	
	@Autowired GraphDatabase graphDatabase;
	@Autowired FractalRepository fractalRepository;


	
	private final static Logger LOG = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping("/")
    public String index() {
        return "redirect: /listFractals";
    }
    
    @RequestMapping("/listFractals")
    @Transactional
    public String listFractals(Model model, HttpServletRequest request) {
        model.addAttribute("page", "listFractals");
    	List<Fractal> fractals = new ArrayList<Fractal>();
    	
		Transaction tx = graphDatabase.beginTx();
		try {
	    	Iterable<Fractal> iterable = fractalRepository.findAll();
	    	for (Fractal fractal : iterable) {
	    		fractals.add(fractal);
	    		if (fractal.getImage() != null) {
		    		request.getSession().setAttribute(fractal.getImageUUID(), fractal.getImage());
	    		}
	    	}
			tx.success();
		} finally {
			tx.close();
		}
		model.addAttribute("fractals", fractals);
        return "greeting";
    }

    @RequestMapping("/listusers")
    public String listUsers(Model model) {
    	List<User> users;
    	
    	users = initUsers();
    	model.addAttribute("users", users);
    	return "listusers";
    }

    @RequestMapping("/mandelbrot")
    public String mandelbrot(
    		Fractal fractal, final boolean reset, final BindingResult bindingResult, 
    		HttpServletRequest request,
    		Model model) {
    	model.addAttribute("page", "mandelbrot");
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
		final String imgId = UUID.randomUUID().toString();
		long oldTime = System.currentTimeMillis();
		session.setAttribute(imgId, mService.computeMandelBrotPng(fractal));
		fractal.setImageUUID(imgId);
		long time = System.currentTimeMillis();
		long delta = time - oldTime;
		LOG.debug("Async call time: " + delta + "ms");
		model.addAttribute("fractal", fractal);
    	return "mandelbrot";
    }

    @RequestMapping("/mandelbrot/save")
    public String mandelbrotSave(
    		Fractal fractal, final BindingResult bindingResult, 
    		HttpServletRequest request,
    		Model model) {
    	HttpSession session = request.getSession();
		Transaction tx = graphDatabase.beginTx();
		try {
			byte [] img = ((Future<byte[]>) session.getAttribute(fractal.getImageUUID())).get();
			fractal.setImage(img);
			fractalRepository.save(fractal);
			tx.success();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			tx.close();
		}
//		session.setAttribute("fractal", fractal);
		model.addAttribute("fractal", fractal);
    	return "redirect:/mandelbrot";
    }

    
    
    @RequestMapping(value = "/getMandelbrotImage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getMandelbrotImage(String imgId, HttpServletRequest request) throws MandelbrotServiceException, InterruptedException, ExecutionException {
    	byte[] img = null;
    	Object idObject = request.getSession().getAttribute(imgId);
    	if (idObject instanceof byte []) {
    		img = (byte []) idObject;
    	} else if (idObject instanceof Future<?>) {
    		Future<byte[]> imgFuture = (Future<byte[]>) idObject;
        	img = imgFuture.get();
    	} else {
    		throw new SecurityException("Invalid image id.");
    	}
    	return img;
    }

    @RequestMapping(value = "/colorMapImage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] colorMapImage(String color1, String color2, String color3, HttpServletRequest request) throws MandelbrotServiceException, InterruptedException, ExecutionException {
    	byte[] img = mService.computeColorGradientPng(Color.decode(color1), Color.decode(color2), Color.decode(color3));
    	return img;

    }
    
	private List<User> initUsers() {
		List<User> users;
		users = new ArrayList<User>();
    	
    	User u1 = new User();
    	u1.setFirstName("Ingo");
    	u1.setLastName("Weichsel");
    	users.add(u1);

    	User u2 = new User();
    	u2.setFirstName("Ada");
    	u2.setLastName("Stopperich");
    	users.add(u2);
		return users;
	}
    
    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="Ingo") String name, Model model) {
        model.addAttribute("name", name);
    	model.addAttribute("page", "greeting");
        return "greeting";
    }
    
    @RequestMapping("/about")
    public String about(@RequestParam(value="name", required=false, defaultValue="att&amp;ack:\nbla") String name, Model model) {
        model.addAttribute("name", name);
    	model.addAttribute("page", "about");
        return "about";
    }
    @RequestMapping("/contact")
    public String contact(Model model) {
    	model.addAttribute("page", "contact");
        return "contact";
    }

}
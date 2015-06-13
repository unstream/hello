package com.test.hello;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.unstream.mandelbrot.MandelbrotService;
import net.unstream.mandelbrot.MandelbrotServiceException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.hello.domain.Fractal;
import com.test.hello.domain.User;

@Controller
public class HelloController {
	
	@Inject
	MandelbrotService mService;

    @RequestMapping("/")
    public String index() {
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
    		Fractal fractal, final BindingResult bindingResult, 
    		HttpServletRequest request,
    		Model model) {
    	model.addAttribute("page", "mandelbrot");
    	
    	Fractal oldFractal = null;
    	oldFractal = (Fractal) request.getSession().getAttribute("fractal");
		if (oldFractal == null) {
			oldFractal = new Fractal(); 
		}

		if (fractal == null) {
			fractal = new Fractal();
		}

		request.getSession().setAttribute("fractal", fractal);
		model.addAttribute("fractal", fractal);
    	return "mandelbrot";
    }

    @RequestMapping(value = "/getMandelbrotImage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getMandelbrotImage(
    		@RequestParam("c0") final double c0,
    		@RequestParam("c0i") final double c0i,
    		@RequestParam("c1") final double c1,
    		@RequestParam("c1i") final double c1i,
    		@RequestParam("iterations") final int iterations
    		) throws MandelbrotServiceException {

    	return mService.computeMandelBrotPng(c0, c0i, c1, c1i, iterations, 500);

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
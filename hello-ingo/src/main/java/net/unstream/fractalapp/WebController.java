package net.unstream.fractalapp;

import java.security.Principal;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.unstream.fractal.api.UserNotFoundException;
import net.unstream.fractal.api.UserService;
import net.unstream.fractal.api.domain.User;
import net.unstream.fractalapp.security.CustomAuthenticationProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Component
public class WebController implements InitializingBean {
	
	@Autowired private UserService userService;
	@Autowired private CustomAuthenticationProvider customAuthenticationProvider;
	private Consumer<String> userDeletionCallback;

	private final static Logger LOG = LoggerFactory.getLogger(WebController.class);

	/**
	 * Set consumer to be called when a user is deleted.
	 * @param callback Consumer to be called.
	 */
	public void setUserDeletionCallback(Consumer<String> callback) {
		this.userDeletionCallback = callback;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("Initializing database");
		createDefaultUser("eric", "", false);
		createDefaultUser("admin", "Truckle", true);
	}
    
    @RequestMapping("/login")
    public String login(Model model, final HttpServletRequest request) {
    	String referrer = request.getHeader("Referer");
        if (referrer != null){
            request.getSession().setAttribute("url_prior_login", referrer);
        }
        return "login";
    }

    @RequestMapping(value="/profile", method=RequestMethod.GET)
    public String profile(final Model model, final Principal principal) throws UserNotFoundException {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("profileUser", user);
		
        return "profile";
    }

	@RequestMapping(value = "/profileDelete", method = RequestMethod.POST)
    public String profileDelete(Model model, final Principal principal, HttpServletRequest request) throws ServletException {
    	if (userDeletionCallback != null) {
    		userDeletionCallback.accept(principal.getName());
    	}
    	userService.delete(principal.getName());
    	request.logout();
    	return "redirect:mandelbrot";
    }

	@RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String profileSave(final Model model, final User user, Principal principal,
    		String oldPassword, String newPassword) throws UserNotFoundException {
    	
		User oldUser = userService.findByUsername(principal.getName());
		oldUser.setUsername(user.getUsername());
		oldUser.setEmail(user.getEmail());
		if (newPassword.length() > 0 || oldPassword.length() > 0) {
	    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
	    	if (encoder.matches(oldPassword, oldUser.getPassword())) {
	    		oldUser.setPassword(encoder.encode(newPassword));
	    	} else {
	    		throw new RuntimeException("Passwords do not match.");
	    	}
		}
		userService.save(oldUser);
		model.addAttribute("profileUser", oldUser);
        return "profile";
    }

    @RequestMapping("/signup")
    public String signup(final User user, final Model model, final HttpServletRequest request) {
    	String password = user.getPassword();
    	
    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    	user.setPassword(encoder.encode(user.getPassword()));
   		try {
			userService.save(user);
		} catch (Exception e) {
			LOG.error("Could not save profile: " + e.getMessage(), e);
			model.addAttribute("signuperror", "Sign up failed, please choose a different username.");
			return "login";
		}
    	user.setPassword(password);
    	authenticateUserAndSetSession(user, request);
        return "redirect:mandelbrot";
    }
    
    private void authenticateUserAndSetSession(User user, HttpServletRequest request) {
        String username = user.getUsername();
        String password = user.getPassword();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        // generate session if one doesn't exist
        request.getSession();

        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = customAuthenticationProvider.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
    }    
    
	public void createDefaultUser(String name, String password, boolean isAdmin) {
		try {
			userService.findByUsername(name);
		} catch (UserNotFoundException e) {
			User user = new User();
    		user.setUsername(name);
    		user.setAdmin(isAdmin);
        	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        	user.setPassword(encoder.encode(password));
        		userService.save(user);
		}
	}

}
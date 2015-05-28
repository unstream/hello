package com.test.hello;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.test.hello.domain.User;

@Controller
public class HelloController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/listusers")
    public String listUsers(Model model) {
    	List<User> users;
    	
    	users = initUsers();
    	model.addAttribute("users", users);
    	return "listusers";
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
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

    @RequestMapping("/listuser/")
    public List<User> listUsers() {
    	List<User> users = new ArrayList<User>();
    	
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
    public String greeting(@RequestParam(value="name", required=false, defaultValue="att&amp;ack:\nbla") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

}
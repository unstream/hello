package net.unstream.fractalapp;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;

import junit.framework.TestCase;
import net.unstream.fractal.api.UserService;
import net.unstream.fractal.api.domain.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


@WebAppConfiguration
public class ControllerTest extends TestCase {
	private MockMvc mockMvc;
	

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");

        
        WebController webController = new WebController();
        
		UserService userService = mock(UserService.class);
		User user = new User();
		user.setUsername("eric");
		user.setEmail("eric@gmx.de");
		when(userService.findByUsername("eric")).thenReturn(user);
		ReflectionTestUtils.setField(webController, "userService", userService);

		
		
		mockMvc = MockMvcBuilders.standaloneSetup(webController)
                                 .setViewResolvers(viewResolver)
                                 .build();
	}

	@Test
	public void testLogin() throws Exception {
		UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(
				"eric", "");
		SecurityContextHolder.getContext().setAuthentication(principal);

		MockHttpServletRequestBuilder getRequest = get("/login").accept(
				MediaType.TEXT_HTML);

		ResultActions results = mockMvc.perform(getRequest);

		results.andExpect(status().isOk());
		results.andExpect(view().name("login"));
	}

	@Test
	public void testProfile() throws Exception {

		Principal principal = new Principal() {
	        @Override
	        public String getName() {
	            return "eric";
	        }
	    };
		MockHttpServletRequestBuilder getRequest = get("/profile").accept(
				MediaType.TEXT_HTML).principal(principal);

	    ResultActions results = mockMvc.perform(getRequest)
	    		.andExpect(status().isOk());		
	    		

		results.andExpect(status().isOk());
		results.andExpect(view().name("profile"));
		results.andExpect(model().attribute("profileUser", hasProperty("username", is("eric"))));
		results.andExpect(model().attribute("profileUser", hasProperty("email", is("eric@gmx.de"))));
	}
}

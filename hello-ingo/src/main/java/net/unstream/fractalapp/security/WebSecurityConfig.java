package net.unstream.fractalapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final AuthenticationProvider authenticationProvider;
	
	private final UserDetailsService userDetailsService;

	public WebSecurityConfig(AuthenticationProvider authenticationProvider, UserDetailsService userDetailsService) {
		this.authenticationProvider = authenticationProvider;
		this.userDetailsService = userDetailsService;
	}

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.authorizeRequests()
                .antMatchers("/", "/mandelbrot*", "/mandelbrot/*", "/getMandelbrotImage", "/listFractals",  
                		"/signup", "/about", "/image", "/bigimage.png", 
                		"/js/**", "/dist/**", "/assets/**", "/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .successHandler(successHandler())
                .permitAll()
                .and()
            .logout()
            	.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) 
                .permitAll()
                .and()
             .anonymous()
             	.principal("anonymous")
             	.and()
             .rememberMe().tokenRepository(persistentTokenRepository())
             	.key("fractal-key")
             	.tokenValiditySeconds(1209600);
    }
	
	@Bean
	public AuthenticationSuccessHandler successHandler() {
	    return new CustomLoginSuccessHandler("/");
	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		PersistentTokenRepository db = new CustomPersistentTokenRepository();
		return db;
	}
	
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	auth.authenticationProvider(authenticationProvider);
    	auth.userDetailsService(userDetailsService);
    }
}
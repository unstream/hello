package com.test.hello.security;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Inject
	@Named("customAuthenticationProvider")
    private AuthenticationProvider authenticationProvider;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/mandelbrot", "/getMandelbrotImage", "/listFractals",  
                		"/signup", "/about", "/image", 
                		"/js/**", "/dist/**", "/assets/**", "/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
            	.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) 
                .permitAll();
    }
    

	
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	auth.authenticationProvider(authenticationProvider);
    	auth.inMemoryAuthentication()
    			.withUser("admin").password("Truckle").authorities("ROLE_ADMIN", "ROLE_USER").and()
    			.withUser("eric").password("").authorities("ROLE_USER");
    }
}
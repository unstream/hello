package net.unstream.fractalapp.security;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Inject
	@Named("customAuthenticationProvider")
    private AuthenticationProvider authenticationProvider;
	
	@Inject
	@Named("userDetailsService")
    private UserDetailsService userDetailsService;

//	@Inject
//	@Named("rememberMeServices")
//	private RememberMeServices rememberMeServices;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/mandelbrot", "/getMandelbrotImage", "/listFractals",  
                		"/signup", "/about", "/image", "/bigimage.png", 
                		"/js/**", "/dist/**", "/assets/**", "/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
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
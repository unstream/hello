package net.unstream.fractalapp.security;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Named("customAuthenticationProvider")
public class CustomAuthenticationProvider implements AuthenticationProvider {
 
	@Lazy @Autowired UserDetailsService userDetailsService;

	
	@Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
    	
    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    	UserDetails details = userDetailsService.loadUserByUsername(name);
    	if ((details != null) && (encoder.matches(password, details.getPassword()))) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(name, password, details.getAuthorities());
			return authenticationToken;
        } else {
            throw new BadCredentialsException("Authentication error.");
        }
    }
 	
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
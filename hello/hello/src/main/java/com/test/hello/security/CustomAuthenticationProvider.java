package com.test.hello.security;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import net.unstream.mandelbrot.MandelbrotServiceException;
import net.unstream.mandelbrot.User;
import net.unstream.mandelbrot.UserRepository;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Named("customAuthenticationProvider")
public class CustomAuthenticationProvider implements AuthenticationProvider {
 
	@Lazy @Autowired GraphDatabase graphDatabase;
	@Lazy @Autowired UserRepository userRepository;

	
	@Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
 
    	Transaction tx = graphDatabase.beginTx();
    	User user = null;
    	try {
			user = userRepository.findByUsername(name);
			tx.success();
		} catch (Exception e) {
			throw new MandelbrotServiceException(e);
		} finally {
			tx.close();
		}
        if ((user != null) && (user.getPassword().equals(password))) {
            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority("USER_ROLE"));
            if (user.isAdmin()) {
            	grantedAuths.add(new SimpleGrantedAuthority("ADMIN_ROLE"));            }
            return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
        } else {
            throw new CustomAuthenticationException("Authentication error");
        }
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
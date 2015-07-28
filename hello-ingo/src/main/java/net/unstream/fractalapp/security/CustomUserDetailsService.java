package net.unstream.fractalapp.security;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import net.unstream.fractal.api.UserNotFoundException;
import net.unstream.fractal.api.UserService;
import net.unstream.fractal.api.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Named("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Lazy @Autowired UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

    	User user = null;
    	UserDetails details = null;
			try {
				user = userService.findByUsername(username);
			} catch (UserNotFoundException e) {
				throw new UsernameNotFoundException(e.getMessage(), e);
			}
			if (user != null) {
		        List<GrantedAuthority> grantedAuths = new ArrayList<>();
		        grantedAuths.add(new SimpleGrantedAuthority(Authorities.ROLE_USER));
		        if (user.isAdmin()) {
		        	grantedAuths.add(new SimpleGrantedAuthority(Authorities.ROLE_ADMIN));
		        }
				details = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuths);
			}
    	
		return details;
	}

}

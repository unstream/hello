package net.unstream.fractalapp.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Named;

import net.unstream.mandelbrot.MandelbrotServiceException;
import net.unstream.mandelbrot.User;
import net.unstream.mandelbrot.UserRepository;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Named("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Lazy @Autowired GraphDatabase graphDatabase;
	@Lazy @Autowired UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

    	Transaction tx = graphDatabase.beginTx();
    	User user = null;
    	UserDetails details = null;
    	try {
			user = userRepository.findByUsername(username);
			if (user !=null) {
		        List<GrantedAuthority> grantedAuths = new ArrayList<>();
		        grantedAuths.add(new SimpleGrantedAuthority(Authorities.ROLE_USER));
		        if (user.isAdmin()) {
		        	grantedAuths.add(new SimpleGrantedAuthority(Authorities.ROLE_ADMIN));
		        }
				details = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuths);
			}
			tx.success();
		} catch (Exception e) {
			throw new MandelbrotServiceException(e);
		} finally {
			tx.close();
		}
    	
		return details;
	}

}

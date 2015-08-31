package net.unstream.fractal.db;

import net.unstream.fractal.api.UserNotFoundException;
import net.unstream.fractal.api.UserService;
import net.unstream.fractal.api.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class UserServiceImpl implements UserService {

	@Lazy @Autowired private GraphDatabase graphDatabase;
	@Autowired private UserRepository userRepository;

	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.UserService#findByUsername(java.lang.String)
	 */
	@Transactional(readOnly=true)
	public User findByUsername(final String name) throws UserNotFoundException {
		User user = userRepository.findByUsername(name);
		if (user == null) { 
			throw new UserNotFoundException("User with name '" + name + "' not found.");
		}
		return user;
	}

	/* (non-Javadoc)
	 * @see net.unstream.fractal.api.UserService#save(net.unstream.fractal.api.domain.User)
	 */
	@Transactional
	public User save(User user) {
   		return userRepository.save(user);
	}
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.api.UserService#delete(java.lang.String)
	 */
	@Transactional
	public void delete(final String name) {
		User user = userRepository.findByUsername(name);
		userRepository.delete(user);
	}
	
}

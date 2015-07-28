package net.unstream.fractal.db;

import net.unstream.fractal.api.UserNotFoundException;
import net.unstream.fractal.api.UserService;
import net.unstream.fractal.api.domain.User;

import org.neo4j.graphdb.Transaction;
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
	public User findByUsername(final String name) throws UserNotFoundException {
		Transaction tx = graphDatabase.beginTx();
		try {
			User user = userRepository.findByUsername(name);
			if (user == null) { 
				throw new UserNotFoundException("User with name '" + name + "' not found.");
			}
			return user;
		} catch (IllegalArgumentException e) {
			tx.failure();
			throw e;
		} finally {
			tx.success();
			tx.close();
		}
	}

	@Transactional
	public User save(User user) {
		Transaction tx = graphDatabase.beginTx();
    	try {
    		return userRepository.save(user);
    	} catch (Exception e) {
    		tx.failure();
    		throw e;
		} finally {
			tx.success();
			tx.close();
		}
	}
	
}

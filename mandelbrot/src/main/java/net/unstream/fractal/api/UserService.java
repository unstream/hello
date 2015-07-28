package net.unstream.fractal.api;

import net.unstream.fractal.api.domain.User;

/**
 * Persistence service to store and retrieve users. 
 *  This service defines the transactional boundary.
 */
public interface UserService {

	/**
	 * Find a user by its name.
	 *
	 * @param name the name of the user
	 * @return the user
	 * @throws UserNotFoundException if no user is found
	 */
	public User findByUsername(String name)
			throws UserNotFoundException;

	/**
	 * Save.
	 *
	 * @param user the user
	 * @return the saved user
	 */
	public User save(User user);

}
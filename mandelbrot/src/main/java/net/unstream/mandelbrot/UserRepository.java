package net.unstream.mandelbrot;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	User findById(long id);
	
	User findByUsername(String name);

}
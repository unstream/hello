package net.unstream.fractal.db;

import net.unstream.fractal.api.domain.User;

import org.springframework.data.repository.CrudRepository;
public interface UserRepository extends CrudRepository<User, Long> {

	User findById(long id);
	User findByUsername(String name);

}
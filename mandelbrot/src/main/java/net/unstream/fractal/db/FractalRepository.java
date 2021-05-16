package net.unstream.fractal.db;

import net.unstream.fractal.api.domain.Fractal;

import net.unstream.fractal.api.domain.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FractalRepository extends MongoRepository<Fractal, String> {

	//Optional<Fractal> findById(String id);
	
	  //@Query("MATCH (f:Fractal) RETURN f SKIP {0} LIMIT {1}")
	  //public List<Fractal> findAll(long skip, long limit);

}
package net.unstream.fractal.db;

import java.util.List;

import net.unstream.fractal.api.domain.Fractal;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;


public interface FractalRepository extends GraphRepository<Fractal> {

	Fractal findById(long id);
	
	  @Query("MATCH (f:Fractal) RETURN f SKIP {0} LIMIT {1}")
	  public List<Fractal> findAll(long skip, long limit);

}
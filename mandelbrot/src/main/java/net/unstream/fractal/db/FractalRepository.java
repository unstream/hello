package net.unstream.fractal.db;

import net.unstream.fractal.api.domain.Fractal;

import org.springframework.data.repository.CrudRepository;

public interface FractalRepository extends CrudRepository<Fractal, Long> {

	Fractal findById(long id);

}
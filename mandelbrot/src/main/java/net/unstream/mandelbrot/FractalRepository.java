package net.unstream.mandelbrot;

import org.springframework.data.repository.CrudRepository;

public interface FractalRepository extends CrudRepository<Fractal, Long> {

	Fractal findById(long id);

}
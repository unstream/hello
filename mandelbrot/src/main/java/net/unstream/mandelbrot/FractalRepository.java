package net.unstream.mandelbrot;

import org.springframework.data.repository.CrudRepository;

public interface FractalRepository extends CrudRepository<Fractal, String> {

	Fractal findById(long id);
//
//    Iterable<Fractal> findByTeammatesName(String name);

}
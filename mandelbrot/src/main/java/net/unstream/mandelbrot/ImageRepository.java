package net.unstream.mandelbrot;

import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, Long> {

	Image findById(long id);

}
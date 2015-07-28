package net.unstream.fractal.db;

import net.unstream.fractal.api.domain.Image;

import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, Long> {

	Image findById(long id);

}
package net.unstream.fractal.db;

import net.unstream.fractal.api.domain.Image;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, String> {

	Optional<Image> findById(String id);

}
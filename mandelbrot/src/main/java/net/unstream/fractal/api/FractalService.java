package net.unstream.fractal.api;

import java.util.List;

import net.unstream.fractal.api.domain.Fractal;
import net.unstream.fractal.api.domain.Image;

/**
 * Persistence service to store and retrieve fractals and images (of fractals).
 * This service defines the transactional boundary.
 */
public interface FractalService {

	/**
	 * Find all.
	 *
	 * @return the iterable
	 */
	public abstract List<Fractal> findAll();

	/**
	 * Save the fractal. Delegate to the repository method.
	 *
	 * @param fractal the fractal to save
	 * @return the saved fractal
	 */
	public abstract Fractal save(Fractal fractal);
	
	/**
	 * Delete.
	 *
	 * @param id the of the fractal to delete
	 */
	public abstract void delete(Long id);

	/**
	 * Find fractal by id.
	 *
	 * @param id the id
	 * @return the fractal
	 */
	public abstract Fractal findById(long id);

	/**
	 * Find image by id.
	 *
	 * @param id the id
	 * @return the image entity
	 */
	public abstract Image findImageById(long id);


}
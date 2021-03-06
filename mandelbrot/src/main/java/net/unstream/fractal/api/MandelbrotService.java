package net.unstream.fractal.api;

import java.util.concurrent.Future;

import net.unstream.fractal.api.domain.Colors;
import net.unstream.fractal.api.domain.Fractal;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;

/**
 * Service to create images of the Mandelbrot set.
 *
 */
public interface MandelbrotService {
	/**
	 * 
	 * Compute a PNG image of the Mandelbrot set between the complex numbers C0 and C1.
	 * @param fractal properties of the fractal to create
	 * @return Future PNG image
	 */
	@Async
	Future<byte []> computeMandelBrotPng(final Fractal fractal, final int width, final int height);
	
	/**
	 * Create an image with the color map of height one.
	 * @param fractal containing the colors used for the map
	 * @return PNG image with the color gradient
	 * @throws MandelbrotServiceException
	 */
	byte[] computeColorGradientPng(final Colors colors) throws MandelbrotServiceException;
}

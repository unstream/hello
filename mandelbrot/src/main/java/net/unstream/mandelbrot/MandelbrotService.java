package net.unstream.mandelbrot;

/**
 * Service to create images of the Mandelbrot set.
 *
 */
public interface MandelbrotService {
	/**
	 * 
	 * Compute a PNG image of the Mandelbrot set between the complex numbers C0 and C1.
	 * @param c0  real part of C0
	 * @param c0i imaginary part of C0
	 * @param c1  real part of C1
	 * @param c1i imaginary part of C1
	 * @param iterations maximum number of iteration before the computation should be stopped
	 * @param width of the created image
	 * @return PNG image
	 * @throws MandelbrotServiceException if something goes wrong when creating the image.
	 */
	byte [] computeMandelBrotPng(double c0, double c0i, double c1, double c1i, int iterations, int width) throws MandelbrotServiceException;
}

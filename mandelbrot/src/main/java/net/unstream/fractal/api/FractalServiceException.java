package net.unstream.fractal.api;

/**
 * Thrown when an error in the FractalServiceImpl occurs.
 */
public class FractalServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new FractalServiceImpl exception.
	 *
	 * @param e the exception used for initialization
	 */
	public FractalServiceException(Exception e) {
		super(e.getMessage());
		initCause(e);
	}
}

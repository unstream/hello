package net.unstream.fractal.api;

public class MandelbrotServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MandelbrotServiceException(Exception e) {
		super(e.getMessage());
		initCause(e);
	}
}

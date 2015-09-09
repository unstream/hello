package net.unstream.polynomial;

public class PolynomialParseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PolynomialParseException(String message, Exception e) {
		super(message, e);
	}

	public PolynomialParseException(String message) {
		super(message);
	}
}

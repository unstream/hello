package net.unstream.fractal.api;

public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = 7282755310036241933L;

	public UserNotFoundException(String message) {
		super(message);
	}
}

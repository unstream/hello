package net.unstream.fractalapp.security;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {

	public CustomAuthenticationException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}

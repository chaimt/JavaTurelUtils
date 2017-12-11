package com.force.exception;


public class NonUniqueResultException extends RuntimeException {

	private static final long serialVersionUID = -2808808077708087053L;

	public NonUniqueResultException() {
		super();
	}

	public NonUniqueResultException(final String message) {
		super(message);
	}
}
package org.irenical.fetchy;

public class RequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RequestException(String message, Throwable cause) {
		super(message, cause);
	}

}

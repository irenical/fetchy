package org.irenical.fetchy;

public class ConnectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConnectException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectException(String message) {
		super(message);
	}


}

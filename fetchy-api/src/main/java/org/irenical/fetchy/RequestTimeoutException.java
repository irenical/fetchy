package org.irenical.fetchy;

public class RequestTimeoutException extends RequestException {

	private static final long serialVersionUID = 1L;

	public RequestTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}

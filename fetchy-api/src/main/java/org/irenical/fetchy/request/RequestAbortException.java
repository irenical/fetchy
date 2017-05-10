package org.irenical.fetchy.request;

public class RequestAbortException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RequestAbortException(String message, Throwable cause) {
		super(message, cause);
	}

}

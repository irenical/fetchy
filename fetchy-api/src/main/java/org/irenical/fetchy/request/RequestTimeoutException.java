package org.irenical.fetchy.request;

public class RequestTimeoutException extends RequestAbortException {

	private static final long serialVersionUID = 1L;

	public RequestTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}

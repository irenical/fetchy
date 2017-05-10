package org.irenical.fetchy.connector;

public class ConnectorMissingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConnectorMissingException(String message) {
		super(message);
	}

}

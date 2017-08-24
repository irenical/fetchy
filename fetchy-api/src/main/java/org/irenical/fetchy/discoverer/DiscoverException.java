package org.irenical.fetchy.discoverer;

public class DiscoverException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DiscoverException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiscoverException(String message) {
        super(message);
    }


}

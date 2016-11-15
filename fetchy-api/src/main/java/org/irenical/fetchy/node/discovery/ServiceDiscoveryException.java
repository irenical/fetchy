package org.irenical.fetchy.node.discovery;

public class ServiceDiscoveryException extends Exception {

    public ServiceDiscoveryException(String message) {
        super(message);
    }

    public ServiceDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceDiscoveryException(Throwable cause) {
        super(cause);
    }

    public ServiceDiscoveryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ServiceDiscoveryException() {
    }
}

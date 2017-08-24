package org.irenical.fetchy.balancer;

public class BalanceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BalanceException(String message) {
        super(message);
    }

}

package org.irenical.fetchy.balancer;

public class BalancerMissingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BalancerMissingException(String message) {
        super(message);
    }

}

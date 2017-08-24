package org.irenical.fetchy.request;

@FunctionalInterface
public interface RunFallback {

    void fallback(Throwable cause);

}

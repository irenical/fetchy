package org.irenical.fetchy.request;

@FunctionalInterface
public interface CallFallback<OUTPUT> {

    OUTPUT fallback(Throwable cause);

}

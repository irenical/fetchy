package org.irenical.fetchy;

@FunctionalInterface
public interface CallFallback<OUTPUT> {

	OUTPUT fallback(Throwable cause);

}

package org.irenical.fetchy;

@FunctionalInterface
public interface RunFallback {

	void fallback(Throwable cause);

}

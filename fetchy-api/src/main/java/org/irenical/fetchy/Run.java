package org.irenical.fetchy;

@FunctionalInterface
public interface Run<API, ERROR extends Exception> {

	void run(API stub) throws ERROR;

}

package org.irenical.fetchy;

@FunctionalInterface
public interface Run<OUTPUT, API, ERROR extends Exception> {

	void run(API stub) throws ERROR;

}

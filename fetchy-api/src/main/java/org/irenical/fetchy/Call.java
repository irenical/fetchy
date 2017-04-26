package org.irenical.fetchy;

@FunctionalInterface
public interface Call<OUTPUT, API, ERROR extends Exception> {

	OUTPUT call(API stub) throws ERROR;

}

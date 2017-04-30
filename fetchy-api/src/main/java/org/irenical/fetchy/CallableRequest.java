package org.irenical.fetchy;

public interface CallableRequest<OUTPUT, ERROR extends Exception> {

	OUTPUT execute() throws ERROR;

}

package org.irenical.fetchy.request;

public interface CallableRequest<OUTPUT, ERROR extends Exception> {

    OUTPUT execute() throws ERROR;

}

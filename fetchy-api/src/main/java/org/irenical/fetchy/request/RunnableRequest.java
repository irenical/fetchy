package org.irenical.fetchy.request;

public interface RunnableRequest<ERROR extends Exception> {

    void execute() throws ERROR;

}

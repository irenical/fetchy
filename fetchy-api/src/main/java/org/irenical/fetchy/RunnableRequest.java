package org.irenical.fetchy;

public interface RunnableRequest<ERROR extends Exception> {

	void execute() throws ERROR;

}

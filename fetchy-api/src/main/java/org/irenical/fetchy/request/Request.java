package org.irenical.fetchy.request;

public interface Request<API> {

	public <OUTPUT, ERROR extends Exception> OUTPUT execute() throws ERROR;

}

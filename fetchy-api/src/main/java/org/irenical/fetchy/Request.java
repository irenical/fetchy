package org.irenical.fetchy;

public interface Request<API> {

	public <OUTPUT, ERROR extends Exception> OUTPUT execute() throws ERROR;

}

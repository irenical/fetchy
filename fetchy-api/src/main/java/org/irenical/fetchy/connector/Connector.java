package org.irenical.fetchy.connector;

import java.net.URI;

@FunctionalInterface
public interface Connector<API> {
	
	API connect(URI node) throws ConnectException;
	
}

package org.irenical.fetchy;

import java.net.URI;

@FunctionalInterface
public interface Connector<API> {
	
	API getStub(URI node);
	
}

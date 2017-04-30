package org.irenical.fetchy;

import java.net.URI;
import java.util.List;

@FunctionalInterface
public interface Discoverer {
	
	List<URI> discover(String serviceId) throws DiscoverException;

}

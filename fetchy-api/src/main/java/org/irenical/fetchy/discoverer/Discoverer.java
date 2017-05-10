package org.irenical.fetchy.discoverer;

import org.irenical.fetchy.Node;

import java.util.List;

@FunctionalInterface
public interface Discoverer {

    List<Node> discover(String serviceId) throws DiscoverException;

}

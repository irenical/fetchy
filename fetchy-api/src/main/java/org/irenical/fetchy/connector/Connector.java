package org.irenical.fetchy.connector;

import org.irenical.fetchy.Node;

@FunctionalInterface
public interface Connector<API> {

    API connect(Node node) throws ConnectException;

}

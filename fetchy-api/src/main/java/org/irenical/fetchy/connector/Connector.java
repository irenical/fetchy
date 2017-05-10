package org.irenical.fetchy.connector;

import org.irenical.fetchy.Node;

@FunctionalInterface
public interface Connector<API> {

    Stub<API> connect(Node node) throws ConnectException;

}

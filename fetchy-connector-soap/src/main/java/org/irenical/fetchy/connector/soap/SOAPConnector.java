package org.irenical.fetchy.connector.soap;

import org.irenical.fetchy.Node;
import org.irenical.fetchy.connector.ConnectException;
import org.irenical.fetchy.connector.Connector;
import org.irenical.fetchy.connector.Stub;
import org.irenical.fetchy.connector.soap.filter.ServiceClientFilter;

import javax.xml.ws.Service;
import java.net.URI;

public class SOAPConnector<ENDPOINT extends Service, PORT> implements Connector<PORT> {

    private final Class<ENDPOINT> endpointClass;

    private final Class<PORT> portClass;

    private final ServiceClientFilter[] filters;

    private String address;

    private PORT port;


    public SOAPConnector(Class<ENDPOINT> endpointClass, Class<PORT> portClass) {
        this(endpointClass, portClass, null);
    }

    public SOAPConnector(Class<ENDPOINT> endpointClass, Class<PORT> portClass, ServiceClientFilter[] filters) {
        this.endpointClass = endpointClass;
        this.portClass = portClass;
        this.filters = filters;
    }

    @Override
    public Stub<PORT> connect(Node node) throws ConnectException {
        if (address == null || !node.getAddress().equals(address) || port == null) {
            address = node.getAddress();

            ServiceClient<ENDPOINT, PORT> serviceClient = new ServiceClient<>(endpointClass, portClass,
                    URI.create(node.getAddress()), filters);
            port = serviceClient.getPort();
        }
        return () -> port;
    }

}

package org.irenical.fetchy.connector.soap;

import org.irenical.fetchy.connector.soap.filter.ServiceClientFilter;
import org.irenical.fetchy.connector.soap.filter.ServiceClientMTOMFilter;

import javax.xml.ws.Service;

public class SOAPMTOMConnector<ENDPOINT extends Service, PORT> extends SOAPConnector<ENDPOINT, PORT> {

    public SOAPMTOMConnector(Class<ENDPOINT> endpointClass, Class<PORT> portClass) {
        super(endpointClass, portClass, new ServiceClientFilter[]{new ServiceClientMTOMFilter()});
    }
}

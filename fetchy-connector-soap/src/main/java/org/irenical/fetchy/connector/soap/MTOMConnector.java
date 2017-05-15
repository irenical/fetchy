package org.irenical.fetchy.connector.soap;

import org.irenical.fetchy.connector.soap.filter.ServiceClientFilter;
import org.irenical.fetchy.connector.soap.filter.ServiceClientMTOMFilter;

import javax.xml.ws.Service;

public class MTOMConnector<ENDPOINT extends Service, PORT> extends SOAPConnector<ENDPOINT, PORT> {

    public MTOMConnector(Class<ENDPOINT> endpointClass, Class<PORT> portClass) {
        super(endpointClass, portClass, new ServiceClientFilter[]{new ServiceClientMTOMFilter()});
    }
}

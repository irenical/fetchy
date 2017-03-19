package org.irenical.fetchy.service.factory.soap;

import org.irenical.fetchy.service.factory.soap.filter.ServiceClientFilter;
import org.irenical.fetchy.service.factory.soap.filter.ServiceClientMTOMFilter;

import javax.xml.ws.Service;

public class SOAPServiceMTOMDiscoveryFactory< PORT, ENDPOINT extends Service> extends SOAPServiceDiscoveryFactory< PORT, ENDPOINT > {

    public SOAPServiceMTOMDiscoveryFactory(String id, Class<PORT> portClass, Class<ENDPOINT> endpointClass, String serviceId) {
        super(id, portClass, endpointClass, serviceId, new ServiceClientFilter[] { new ServiceClientMTOMFilter() } );
    }

}

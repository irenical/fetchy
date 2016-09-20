package org.irenical.fetchy.service.factory.soap;

import org.irenical.fetchy.service.Stub;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;

import javax.xml.ws.Service;

public class SOAPServiceDiscoveryFactory< PORT, ENDPOINT extends Service > extends ServiceDiscoveryFactory< PORT > {

    private final Class< PORT > portClass;

    private final Class< ENDPOINT > endpointClass;

    private final String serviceId;

    public SOAPServiceDiscoveryFactory( String id, Class< PORT > portClass, Class< ENDPOINT > endpointClass, String serviceId ) {
        super( id );
        this.portClass = portClass;
        this.endpointClass = endpointClass;
        this.serviceId = serviceId;
    }

    @Override
    public Stub<PORT> createService() {
        SOAPServiceExecutor<PORT, ENDPOINT> serviceExecutor = new SOAPServiceExecutor<>(portClass, endpointClass, serviceId);

        serviceExecutor.setServiceNodeDiscovery( getServiceNodeDiscovery() );
        serviceExecutor.setServiceNodeBalancer( getServiceNodeBalancer() );

        return serviceExecutor;
    }

}

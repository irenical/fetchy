package org.irenical.fetchy.service.factory.soap;

import org.irenical.fetchy.service.Stub;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;
import org.irenical.fetchy.service.factory.soap.filter.ServiceClientFilter;

import javax.xml.ws.Service;

public class SOAPServiceDiscoveryFactory< PORT, ENDPOINT extends Service > extends ServiceDiscoveryFactory< PORT > {

    private final Class< PORT > portClass;

    private final Class< ENDPOINT > endpointClass;

    private final String serviceId;

    private final ServiceClientFilter[] filters;

    public SOAPServiceDiscoveryFactory( String id, Class< PORT > portClass, Class< ENDPOINT > endpointClass, String serviceId ) {
        this( id, portClass, endpointClass, serviceId, null );
    }

    public SOAPServiceDiscoveryFactory( String id, Class< PORT > portClass, Class< ENDPOINT > endpointClass, String serviceId,
                                        ServiceClientFilter[] filters ) {
        super( id );

        this.portClass = portClass;
        this.endpointClass = endpointClass;
        this.serviceId = serviceId;
        this.filters = filters;
    }

    @Override
    public Stub<PORT> createService() {
        SOAPServiceExecutor<ENDPOINT, PORT > serviceExecutor = new SOAPServiceExecutor<>( endpointClass, portClass, serviceId, filters );

        serviceExecutor.setServiceNodeDiscovery( getServiceNodeDiscovery() );
        serviceExecutor.setServiceNodeBalancer( getServiceNodeBalancer() );

        return serviceExecutor;
    }

}

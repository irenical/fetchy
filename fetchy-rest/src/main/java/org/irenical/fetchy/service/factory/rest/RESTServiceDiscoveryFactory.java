package org.irenical.fetchy.service.factory.rest;

import org.irenical.fetchy.service.Stub;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;

public class RESTServiceDiscoveryFactory<IFACE> extends ServiceDiscoveryFactory<IFACE> {

    private final String serviceId;

    public RESTServiceDiscoveryFactory(Class<IFACE> ifaceClass, String serviceId) {
        super( ifaceClass );
        this.serviceId = serviceId;
    }

    @Override
    public Stub<IFACE> createService() {
        RESTServiceExecutor<IFACE> serviceExecutor = new RESTServiceExecutor<>(getServiceInterface(), serviceId);

        serviceExecutor.setServiceNodeDiscovery( getServiceNodeDiscovery() );
        serviceExecutor.setServiceNodeBalancer( getServiceNodeBalancer() );

        return serviceExecutor;
    }

}

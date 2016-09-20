package org.irenical.fetchy.service.factory.rest;

import org.irenical.fetchy.service.Stub;
import org.irenical.fetchy.service.factory.ServiceDiscoveryExecutor;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;

public abstract class RESTServiceDiscoveryFactory<IFACE> extends ServiceDiscoveryFactory<IFACE> {

    public RESTServiceDiscoveryFactory( String id ) {
        super( id );
    }

    @Override
    public Stub<IFACE> createService() {
        ServiceDiscoveryExecutor<IFACE, IFACE> serviceExecutor = createServiceExecutor();

        setupServiceExecutor( serviceExecutor );

        return serviceExecutor;
    }

    private void setupServiceExecutor( ServiceDiscoveryExecutor< IFACE, IFACE > serviceExecutor ) {
        serviceExecutor.setServiceNodeDiscovery( getServiceNodeDiscovery() );
        serviceExecutor.setServiceNodeBalancer( getServiceNodeBalancer() );
    }

    protected abstract ServiceDiscoveryExecutor< IFACE, IFACE > createServiceExecutor();

}

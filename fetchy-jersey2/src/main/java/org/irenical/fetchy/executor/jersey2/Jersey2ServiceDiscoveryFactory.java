package org.irenical.fetchy.executor.jersey2;

import org.irenical.fetchy.service.Stub;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;

import javax.ws.rs.client.WebTarget;

public class Jersey2ServiceDiscoveryFactory extends ServiceDiscoveryFactory<WebTarget> {

    private final String serviceId;

    public Jersey2ServiceDiscoveryFactory(String id, String serviceId) {
        super( id );
        this.serviceId = serviceId;
    }

    @Override
    public Stub<WebTarget> createService() {
        Jersey2ServiceExecutor executor = new Jersey2ServiceExecutor(serviceId);

        executor.setServiceNodeBalancer( getServiceNodeBalancer() );
        executor.setServiceNodeDiscovery( getServiceNodeDiscovery() );

        return executor;
    }

}

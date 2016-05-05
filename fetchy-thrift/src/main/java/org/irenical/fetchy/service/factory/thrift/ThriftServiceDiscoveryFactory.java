package org.irenical.fetchy.service.factory.thrift;

import org.irenical.fetchy.service.Stub;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;

public class ThriftServiceDiscoveryFactory<IFACE, CLIENT extends IFACE> extends ServiceDiscoveryFactory<IFACE> {

    private final Class<CLIENT> clientClass;

    private final String serviceId;

    public ThriftServiceDiscoveryFactory(Class<IFACE> serviceInterface, Class<CLIENT> clientClass, String serviceId) {
        super(serviceInterface);

        this.clientClass = clientClass;
        this.serviceId = serviceId;
    }

    @Override
    public Stub<IFACE> createService() {
        ThriftServiceExecutor<IFACE, CLIENT> serviceExecutor = new ThriftServiceExecutor<>(clientClass, serviceId);

        serviceExecutor.setServiceNodeDiscovery( getServiceNodeDiscovery() );
        serviceExecutor.setServiceNodeBalancer( getServiceNodeBalancer() );

        return serviceExecutor;
    }

}

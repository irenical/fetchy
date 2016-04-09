package org.irenical.fetchy.service.factory.thrift;

import org.apache.thrift.TServiceClient;
import org.irenical.fetchy.node.balancer.ServiceNodeBalancer;
import org.irenical.fetchy.node.discovery.ServiceNodeDiscovery;
import org.irenical.fetchy.service.ServiceExecutor;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;

public class ThriftServiceDiscoveryFactory< IFACE, CLIENT extends TServiceClient > extends ServiceDiscoveryFactory< IFACE > {

    private final Class< CLIENT > clientClass;

    private final String serviceId;

    public ThriftServiceDiscoveryFactory(Class<IFACE> serviceInterface, Class<CLIENT> clientClass, String serviceId) {
        super( serviceInterface );

        this.clientClass = clientClass;
        this.serviceId = serviceId;
    }

    @Override
    public ServiceExecutor<IFACE> createService() {
        ThriftServiceExecutor<IFACE, CLIENT> serviceExecutor = new ThriftServiceExecutor<>(clientClass, serviceId);

        ServiceNodeDiscovery serviceNodeLocator = getServiceNodeDiscovery();
        if (serviceNodeLocator != null ) {
            serviceExecutor.setServiceNodeDiscovery(serviceNodeLocator);
        }
        ServiceNodeBalancer serviceNodeBalancer = getServiceNodeBalancer();
        if ( serviceNodeBalancer != null ) {
            serviceExecutor.setServiceNodeBalancer(serviceNodeBalancer);
        }
        return serviceExecutor;
    }

}

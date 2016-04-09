package org.irenical.fetchy.service.factory.rest;


import org.irenical.fetchy.node.balancer.ServiceNodeBalancer;
import org.irenical.fetchy.node.discovery.ServiceNodeDiscovery;
import org.irenical.fetchy.service.ServiceExecutor;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;

public class RESTServiceDiscoveryFactory< IFACE > extends ServiceDiscoveryFactory< IFACE > {

    private final String serviceId;

    public RESTServiceDiscoveryFactory(Class< IFACE > ifaceClass, String serviceId) {
        super( ifaceClass );
        this.serviceId = serviceId;
    }

    @Override
    public ServiceExecutor< IFACE > createService() {
        RESTServiceExecutor<IFACE> serviceExecutor = new RESTServiceExecutor<>(getServiceInterface(), serviceId);

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

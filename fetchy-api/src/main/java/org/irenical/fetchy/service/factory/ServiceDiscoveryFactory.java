package org.irenical.fetchy.service.factory;

import org.irenical.fetchy.node.balancer.ServiceNodeBalancer;
import org.irenical.fetchy.node.discovery.ServiceNodeDiscovery;

public abstract class ServiceDiscoveryFactory< IFACE > extends BaseServiceFactory< IFACE > {

    private ServiceNodeDiscovery serviceNodeDiscovery;

    private ServiceNodeBalancer serviceNodeBalancer;


    public ServiceDiscoveryFactory(Class<IFACE> serviceInterface) {
        super(serviceInterface);
    }

    public ServiceNodeDiscovery getServiceNodeDiscovery() {
        return serviceNodeDiscovery;
    }

    /**
     * Don't forget to call serviceNodeDiscovery.start().
     *
     * @param serviceNodeDiscovery    the node locator implementation
     */
    public void setServiceNodeDiscovery(ServiceNodeDiscovery serviceNodeDiscovery) {
        this.serviceNodeDiscovery = serviceNodeDiscovery;
    }

    public ServiceNodeBalancer getServiceNodeBalancer() {
        return serviceNodeBalancer;
    }

    /**
     * Don't forget to call serviceNodeBalancer.start().
     *
     * @param serviceNodeBalancer   the node balancer implementation
     */
    public void setServiceNodeBalancer(ServiceNodeBalancer serviceNodeBalancer) {
        this.serviceNodeBalancer = serviceNodeBalancer;
    }


}

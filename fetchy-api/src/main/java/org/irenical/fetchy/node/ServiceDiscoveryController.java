package org.irenical.fetchy.node;

import org.irenical.fetchy.node.balancer.ServiceNodeBalancer;
import org.irenical.fetchy.node.discovery.ServiceNodeDiscovery;
import org.irenical.lifecycle.LifeCycle;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

public class ServiceDiscoveryController implements LifeCycle {

    private ServiceNodeDiscovery serviceNodeDiscovery;

    private ServiceNodeBalancer nodeBalancer;


    public ServiceDiscoveryController() {

    }

    public void setServiceNodeDiscovery(ServiceNodeDiscovery serviceNodeDiscovery) {
        this.serviceNodeDiscovery = serviceNodeDiscovery;
    }

    public void setNodeBalancer(ServiceNodeBalancer nodeBalancer) {
        this.nodeBalancer = nodeBalancer;
    }

    @Override
    public void start() throws Exception {
        loadServiceDiscovery();
        loadServiceBalancer();
    }

    @Override
    public void stop() throws Exception {
        if ( serviceNodeDiscovery != null ) {
            serviceNodeDiscovery.stop();
            serviceNodeDiscovery = null;
        }
        if ( nodeBalancer != null ) {
            nodeBalancer.stop();
            nodeBalancer = null;
        }
    }

    @Override
    public boolean isRunning() throws Exception {
        return serviceNodeDiscovery != null && serviceNodeDiscovery.isRunning()
                && nodeBalancer != null && nodeBalancer.isRunning();
    }

    public Optional<ServiceNode> get( String serviceId ) {
        List<ServiceNode> nodes = locate(serviceId);
        return choose(nodes);
    }

    private List< ServiceNode > locate(String serviceId ) {
        return serviceNodeDiscovery == null ? null : serviceNodeDiscovery.getServiceNodes( serviceId, true );
    }

    private Optional<ServiceNode> choose( List< ServiceNode > nodes ) {
        return nodeBalancer == null ? chooseDefault( nodes ) : nodeBalancer.getService( nodes );
    }

    private Optional< ServiceNode > chooseDefault( List< ServiceNode > nodes ) {
//        TODO : default balancer implementation
        return Optional.empty();
    }

    private void loadServiceDiscovery() {
        List< ServiceNodeDiscovery > locators = new LinkedList<>();

        ServiceLoader<ServiceNodeDiscovery> serviceNodeLocators = ServiceLoader.load(ServiceNodeDiscovery.class);
        for (ServiceNodeDiscovery serviceNodeLocator : serviceNodeLocators) {
            locators.add( serviceNodeLocator );
        }

        if ( locators.size() > 1 ) {
            throw new RuntimeException( "multiple node locators found - choose one." );
        }
        if ( ! locators.isEmpty() ) {
            ServiceNodeDiscovery serviceNodeLocator = locators.get(0);
            serviceNodeLocator.start();
            setServiceNodeDiscovery(serviceNodeLocator);
        }
    }

    private void loadServiceBalancer() {
        List<ServiceNodeBalancer> balancers = new LinkedList<>();

        for (ServiceNodeBalancer serviceNodeBalancer : ServiceLoader.load(ServiceNodeBalancer.class)) {
            balancers.add(serviceNodeBalancer);
        }

        if ( balancers.size() > 1 ) {
            throw new RuntimeException( "multiple node balancers found - choose one." );
        }
        if ( ! balancers.isEmpty() ) {
            ServiceNodeBalancer serviceNodeBalancer = balancers.get(0);
            serviceNodeBalancer.start();
            setNodeBalancer(serviceNodeBalancer);
        }
    }

}

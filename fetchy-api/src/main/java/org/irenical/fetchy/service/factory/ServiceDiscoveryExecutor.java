package org.irenical.fetchy.service.factory;


import java.util.Optional;

import org.irenical.fetchy.node.ServiceDiscoveryController;
import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.node.balancer.ServiceNodeBalancer;
import org.irenical.fetchy.node.discovery.ServiceNodeDiscovery;
import org.irenical.fetchy.service.Stub;

public abstract class ServiceDiscoveryExecutor<IFACE,CLIENT extends IFACE> implements Stub<IFACE> {

    private final ServiceDiscoveryController serviceDiscoveryController = new ServiceDiscoveryController();

    private final String serviceId;


    public ServiceDiscoveryExecutor( String serviceId ) {
        this.serviceId = serviceId;
    }

    @Override
    public <OUTPUT, ERROR extends Exception> OUTPUT call(ServiceCall<IFACE,OUTPUT,ERROR> callable) throws ERROR {
        CLIENT clientInstance = null;
        try {
            clientInstance = create();
            onBeforeExecute( clientInstance );
            return callable.call( (IFACE) clientInstance );
        } finally {
            onAfterExecute( clientInstance );
        }
    }

    private CLIENT create() {
        Optional<ServiceNode> serviceNode = serviceDiscoveryController.get( serviceId );
        ServiceNode node = serviceNode.orElseThrow(() -> new RuntimeException( "Unable to find a service node" ));

        return newInstance( node );
    }


    protected abstract <ERROR extends Exception> CLIENT newInstance( ServiceNode serviceNode ) throws ERROR;

    protected abstract void onBeforeExecute( CLIENT client );

    protected abstract void onAfterExecute( CLIENT client );

    @Override
    public void start() throws Exception {
        serviceDiscoveryController.start();
    }

    @Override
    public void stop() throws Exception {
        serviceDiscoveryController.stop();
    }

    @Override
    public boolean isRunning() throws Exception {
        return serviceDiscoveryController.isRunning();
    }


    public void setServiceNodeDiscovery(ServiceNodeDiscovery serviceNodeLocator) {
        serviceDiscoveryController.setServiceNodeDiscovery( serviceNodeLocator );
    }

    public void setServiceNodeBalancer(ServiceNodeBalancer serviceNodeBalancer) {
        serviceDiscoveryController.setNodeBalancer( serviceNodeBalancer );
    }

}

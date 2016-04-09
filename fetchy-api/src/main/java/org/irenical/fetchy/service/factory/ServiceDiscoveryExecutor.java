package org.irenical.fetchy.service.factory;


import org.irenical.fetchy.node.ServiceDiscoveryController;
import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.node.balancer.ServiceNodeBalancer;
import org.irenical.fetchy.node.discovery.ServiceNodeDiscovery;
import org.irenical.fetchy.service.ServiceExecutor;

import java.util.Optional;

public abstract class ServiceDiscoveryExecutor< IFACE, CLIENT > implements ServiceExecutor< IFACE > {

    private final ServiceDiscoveryController serviceDiscoveryController = new ServiceDiscoveryController();

    private final String serviceId;


    public ServiceDiscoveryExecutor( String serviceId ) {
        this.serviceId = serviceId;
    }

    @Override
    public <OUTPUT> OUTPUT execute(ServiceCall<IFACE, OUTPUT> callable) throws Exception {
        CLIENT clientInstance = null;
        try {
            clientInstance = create();
            onBeforeExecute( clientInstance );
            return callable.call( ( IFACE ) clientInstance );
        } finally {
            onAfterExecute( clientInstance );
        }
    }

    private CLIENT create() throws Exception {
        Optional<ServiceNode> serviceNode = serviceDiscoveryController.get( serviceId );
        ServiceNode node = serviceNode.orElseThrow(() -> new RuntimeException( "Unable to find a service node" ));

        return newInstance( node );
    }


    protected abstract CLIENT newInstance( ServiceNode serviceNode ) throws Exception;

    protected abstract void onBeforeExecute( CLIENT client ) throws Exception;

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

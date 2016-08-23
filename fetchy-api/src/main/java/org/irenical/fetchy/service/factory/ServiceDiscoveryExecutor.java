package org.irenical.fetchy.service.factory;

import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.node.balancer.ServiceNodeBalancer;
import org.irenical.fetchy.node.discovery.ServiceNodeDiscovery;
import org.irenical.fetchy.service.Stub;

import java.util.List;
import java.util.Optional;

public abstract class ServiceDiscoveryExecutor<IFACE,CLIENT extends IFACE> implements Stub<IFACE> {

    private final String serviceId;

    private ServiceNodeDiscovery nodeDiscovery;

    private ServiceNodeBalancer nodeBalancer;


    public ServiceDiscoveryExecutor( String serviceId ) {
        this.serviceId = serviceId;
    }

    @Override
    public <OUTPUT, ERROR extends Exception> OUTPUT call(ServiceCall<IFACE,OUTPUT,ERROR> callable) throws ERROR {
        CLIENT clientInstance = null;
        try {
            clientInstance = create();
            if ( clientInstance == null ) {
                throw new RuntimeException( "Unable to instantiate a new client" );
            }
            onBeforeExecute( clientInstance );
            return callable.call( (IFACE) clientInstance );
        } finally {
            onAfterExecute( clientInstance );
        }
    }

    @Override
    public <ERROR extends Exception> void run(ServiceRun<IFACE, ERROR> callable) throws ERROR {
        CLIENT clientInstance = null;
        try {
            clientInstance = create();
            if ( clientInstance == null ) {
                throw new RuntimeException( "Unable to instantiate a new client" );
            }
            onBeforeExecute( clientInstance );
            callable.run( (IFACE) clientInstance );
        } finally {
            onAfterExecute( clientInstance );
        }
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public boolean isRunning() throws Exception {
        return nodeDiscovery != null && nodeDiscovery.isRunning()
                && nodeBalancer != null && nodeBalancer.isRunning();
    }

    public void setServiceNodeDiscovery(ServiceNodeDiscovery nodeDiscovery) {
        this.nodeDiscovery = nodeDiscovery;
    }

    public void setServiceNodeBalancer(ServiceNodeBalancer nodeBalancer) {
        this.nodeBalancer = nodeBalancer;
    }


    private < ERROR extends Exception > CLIENT create() throws ERROR {
        Optional<ServiceNode> serviceNode = findServiceNode( serviceId );
        ServiceNode node = serviceNode.orElseThrow(() -> new RuntimeException( "Unable to find a service node" ));

        return newInstance( node );
    }


    protected abstract <ERROR extends Exception> CLIENT newInstance( ServiceNode serviceNode ) throws ERROR;

    protected abstract void onBeforeExecute( CLIENT client );

    protected abstract void onAfterExecute( CLIENT client );


    private Optional<ServiceNode> findServiceNode( String serviceId ) {
        List<ServiceNode> nodes = locate(serviceId);
        return choose(nodes);
    }

    private List< ServiceNode > locate(String serviceId ) {
        return nodeDiscovery == null ? null : nodeDiscovery.getServiceNodes( serviceId, true );
    }

    private Optional<ServiceNode> choose( List< ServiceNode > nodes ) {
        return nodeBalancer == null ? chooseDefault( nodes ) : nodeBalancer.getService( nodes );
    }

    private Optional< ServiceNode > chooseDefault( List< ServiceNode > nodes ) {
        return nodes == null || nodes.isEmpty() ? Optional.empty() : Optional.of( nodes.get( 0 ) );
    }

}

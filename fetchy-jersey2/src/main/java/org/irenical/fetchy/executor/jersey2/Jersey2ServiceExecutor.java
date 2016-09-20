package org.irenical.fetchy.executor.jersey2;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.service.factory.ServiceDiscoveryExecutor;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;


public class Jersey2ServiceExecutor extends ServiceDiscoveryExecutor<WebTarget,WebTarget> {

    public Jersey2ServiceExecutor(String serviceId) {
        super( serviceId );
    }

    @Override
    protected WebTarget newInstance(ServiceNode serviceNode) throws Exception {
        Client client = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .build();

        return client.target(serviceNode.getAddress());
    }

    @Override
    protected void onBeforeExecute(WebTarget iface) {

    }

    @Override
    protected void onAfterExecute( WebTarget iface ) {

    }

}

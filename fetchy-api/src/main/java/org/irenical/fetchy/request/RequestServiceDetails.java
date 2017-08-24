package org.irenical.fetchy.request;

import org.irenical.fetchy.balancer.Balancer;
import org.irenical.fetchy.connector.Connector;
import org.irenical.fetchy.discoverer.Discoverer;

public class RequestServiceDetails<API> {
    private String serviceId;
    private Connector<API> connector;
    private Balancer balancer;
    private Discoverer discoverer;

    public RequestServiceDetails(String serviceId, Connector<API> connector, Balancer balancer, Discoverer discoverer) {
        this.serviceId = serviceId;
        this.connector = connector;
        this.balancer = balancer;
        this.discoverer = discoverer;
    }

    public String getServiceId() {
        return serviceId;
    }

    public Connector<API> getConnector() {
        return connector;
    }

    public Balancer getBalancer() {
        return balancer;
    }

    public Discoverer getDiscoverer() {
        return discoverer;
    }
}

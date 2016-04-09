package org.irenical.fetchy.node.discovery;

import org.irenical.fetchy.node.ServiceNode;
import org.irenical.lifecycle.LifeCycle;

import java.util.List;

public interface ServiceNodeDiscovery extends LifeCycle {

    List<ServiceNode> getServiceNodes(String serviceId, boolean onlyHealthy);

}

package org.irenical.fetchy.node.balancer;

import org.irenical.fetchy.node.ServiceNode;
import org.irenical.lifecycle.LifeCycle;

import java.util.List;
import java.util.Optional;

public interface ServiceNodeBalancer extends LifeCycle {

    Optional<ServiceNode> getService(List<ServiceNode> nodes);

}

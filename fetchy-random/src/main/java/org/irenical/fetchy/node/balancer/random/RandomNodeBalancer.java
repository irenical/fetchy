package org.irenical.fetchy.node.balancer.random;

import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.node.balancer.ServiceNodeBalancer;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomNodeBalancer implements ServiceNodeBalancer {

    private final Random random = new Random();

    @Override
    public Optional<ServiceNode> getService(List<ServiceNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(nodes.get(random.nextInt(nodes.size())));
    }

    @Override
    public <ERROR extends Exception> void start() throws ERROR {

    }

    @Override
    public <ERROR extends Exception> void stop() throws ERROR {

    }

    @Override
    public <ERROR extends Exception> boolean isRunning() throws ERROR {
        return true;
    }
}

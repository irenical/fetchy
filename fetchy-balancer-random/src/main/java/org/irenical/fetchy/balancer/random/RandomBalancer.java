package org.irenical.fetchy.balancer.random;

import org.irenical.fetchy.Node;
import org.irenical.fetchy.balancer.BalanceException;
import org.irenical.fetchy.balancer.Balancer;

import java.util.List;
import java.util.Random;

public class RandomBalancer implements Balancer {

    private final Random random = new Random();

    @Override
    public Node balance(List<Node> nodes) throws BalanceException {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        return nodes.get(random.nextInt(nodes.size()));
    }
}

package org.irenical.fetchy.balancer;

import org.irenical.fetchy.Node;

import java.util.List;

public interface Balancer {

	Node balance(List<Node> nodes) throws BalanceException;

}

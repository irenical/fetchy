package org.irenical.fetchy.balancer;

import java.net.URI;
import java.util.List;

public interface Balancer {
	
	URI balance(List<URI> nodes) throws BalanceException;

}

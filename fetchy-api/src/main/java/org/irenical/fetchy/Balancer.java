package org.irenical.fetchy;

import java.net.URI;
import java.util.List;

public interface Balancer {
	
	URI balance(List<URI> nodes) throws BalanceException;

}

package org.irenical.fetchy;

import java.net.URI;
import java.util.List;

public abstract class ImmutableRequest {
	
	protected Fetchy fetchy;
	
	protected String serviceId;
	
	protected Integer timeoutMillis;
	
	public ImmutableRequest(Fetchy fetchy, String serviceId, Integer timeoutMillis) {
		this.fetchy = fetchy;
		this.serviceId = serviceId;
		this.timeoutMillis = timeoutMillis;
	}
	
	protected URI attemptDiscover() {
		Discoverer disco = fetchy.getServiceDiscoverer(serviceId);
		if (disco != null) {
			List<URI> uris = disco.discover(serviceId);
			Balancer bal = fetchy.getServiceBalancer(serviceId);
			if (bal != null) {
				return bal.balance(uris);
			} else if (uris != null && !uris.isEmpty()) {
				return uris.get(0);
			}
		}
		return null;
	}
	
}

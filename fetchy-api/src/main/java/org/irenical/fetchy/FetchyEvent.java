package org.irenical.fetchy;

import java.net.URI;

public class FetchyEvent<OBJECT> {

	private final String serviceId;
	private final String name;
	private final URI node;
	private final long elapsedMillis;
	private final OBJECT target;
	
	public FetchyEvent(String serviceId, String name, URI node, long elapsedMillis, OBJECT target) {
		this.serviceId = serviceId;
		this.name = name;
		this.node = node;
		this.elapsedMillis = elapsedMillis;
		this.target = target;
	}
	
	public URI getNode() {
		return node;
	}

	public long getElapsedMillis() {
		return elapsedMillis;
	}
	
	public String getName() {
		return name;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	
	public OBJECT getTarget() {
		return target;
	}
	
}

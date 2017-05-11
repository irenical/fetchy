package org.irenical.fetchy.event;

import org.irenical.fetchy.Node;

public class FetchyEvent<OBJECT> {

	private final String serviceId;
	private final String name;
	private final Node node;
	private final long elapsedMillis;
	private final OBJECT target;
	
	public FetchyEvent(String serviceId, String name, Node node, long elapsedMillis, OBJECT target) {
		this.serviceId = serviceId;
		this.name = name;
		this.node = node;
		this.elapsedMillis = elapsedMillis;
		this.target = target;
	}
	
	public Node getNode() {
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

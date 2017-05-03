package org.irenical.fetchy;

import java.net.URI;

public class RequestResolvedEvent {

	private long elapsedMillis;

	private String serviceId;

	private URI node;
	
	private Throwable error;

	private String name;
	
	protected void setError(Throwable error) {
		this.error = error;
	}
	
	protected void setElapsedMillis(long elapsedMillis) {
		this.elapsedMillis = elapsedMillis;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setNode(URI node) {
		this.node = node;
	}

	protected void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public long getElapsedMillis() {
		return elapsedMillis;
	}

	public String getName() {
		return name;
	}

	public URI getNode() {
		return node;
	}

	public String getServiceId() {
		return serviceId;
	}
	
	public Throwable getError() {
		return error;
	}

}

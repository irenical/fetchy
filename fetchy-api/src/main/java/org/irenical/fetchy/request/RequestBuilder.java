package org.irenical.fetchy.request;

import org.irenical.fetchy.Fetchy;

public class RequestBuilder<API> {

	private Fetchy fetchy;

	private String serviceId;
	
	private String name;

	public RequestBuilder(Fetchy fetchy) {
		this.fetchy = fetchy;
	}

	public RequestBuilder<API> service(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}
	
	public RequestBuilder<API> name(String name) {
		this.name = name;
		return this;
	}
	
	public <OUTPUT, ERROR extends Exception> CallableRequestBuilder<OUTPUT, API, ERROR> callable(Call<OUTPUT, API, ERROR> callable) {
		CallableRequestBuilder<OUTPUT, API, ERROR> result = new CallableRequestBuilder<>(fetchy);
		result.service(serviceId);
		result.name(name);
		result.callable(callable);
		return result;
	}
	
	public <OUTPUT, ERROR extends Exception> RunnableRequestBuilder<API, ERROR> runnable(Run<API, ERROR> runnable) {
		RunnableRequestBuilder<API, ERROR> result = new RunnableRequestBuilder<>(fetchy);
		result.service(serviceId);
		result.name(name);
		result.runnable(runnable);
		return result;
	}

}

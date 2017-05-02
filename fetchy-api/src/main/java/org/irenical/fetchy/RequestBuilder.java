package org.irenical.fetchy;

public class RequestBuilder<API> {

	private Fetchy fetchy;

	private String serviceId;

	private Integer timeoutMillis;

	public RequestBuilder(Fetchy fetchy) {
		this.fetchy = fetchy;
	}

	public RequestBuilder<API> service(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}
	
	public RequestBuilder<API> timeout(int millis) {
		this.timeoutMillis = millis;
		return this;
	}
	
	public <OUTPUT, ERROR extends Exception> CallableRequestBuilder<OUTPUT, API, ERROR> callable(Call<OUTPUT, API, ERROR> callable) {
		CallableRequestBuilder<OUTPUT, API, ERROR> result = new CallableRequestBuilder<>(fetchy);
		result.service(serviceId);
		result.timeout(timeoutMillis);
		result.callable(callable);
		return result;
	}
	
	public <OUTPUT, ERROR extends Exception> RunnableRequestBuilder<API, ERROR> runnable(Run<API, ERROR> runnable) {
		RunnableRequestBuilder<API, ERROR> result = new RunnableRequestBuilder<>(fetchy);
		result.service(serviceId);
		result.timeout(timeoutMillis);
		result.runnable(runnable);
		return result;
	}

}

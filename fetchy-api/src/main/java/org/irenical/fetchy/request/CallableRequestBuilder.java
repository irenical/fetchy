package org.irenical.fetchy.request;

import org.irenical.fetchy.engine.FetchyEngine;

public class CallableRequestBuilder<OUTPUT, API, ERROR extends Exception> {

	private final FetchyEngine engine;

	private CallServiceDetails<API> serviceDetails;
	
	private String name;

	private Integer timeoutMillis;

	private Call<OUTPUT, API, ERROR> callable;

	private CallFallback<OUTPUT> fallback;

	public CallableRequestBuilder(FetchyEngine engine, CallServiceDetails<API> serviceDetails) {
		this.engine = engine;
		this.serviceDetails = serviceDetails;
	}

	public CallableRequestBuilder<OUTPUT, API, ERROR> timeout(Integer timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
		return this;
	}
	
	public CallableRequestBuilder<OUTPUT, API, ERROR> name(String name) {
		this.name = name;
		return this;
	}

	public CallableRequestBuilder<OUTPUT, API, ERROR> callable(Call<OUTPUT, API, ERROR> lambda) {
		this.callable = lambda;
		return this;
	}

	public CallableRequestBuilder<OUTPUT, API, ERROR> fallback(CallFallback<OUTPUT> fallback) {
		this.fallback = fallback;
		return this;
	}

	public CallableRequest<OUTPUT, ERROR> build() {
		return new ImmutableCallableRequest<>(name, engine, serviceDetails, timeoutMillis, callable, fallback);
	}

}

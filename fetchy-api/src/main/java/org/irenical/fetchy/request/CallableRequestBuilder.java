package org.irenical.fetchy.request;

import org.irenical.fetchy.engine.FetchyEngine;

public class CallableRequestBuilder<OUTPUT, API, ERROR extends Exception> {

	private final FetchyEngine engine;

	private RequestServiceDetails<API> serviceDetails;
	
	private String name;

	private Integer timeoutMillis;

	private Call<OUTPUT, API, ?> callable;

	private CallFallback<OUTPUT> fallback;

	public CallableRequestBuilder(FetchyEngine engine, RequestServiceDetails<API> serviceDetails) {
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

	public CallableRequestBuilder<OUTPUT, API, RuntimeException> fallback(CallFallback<OUTPUT> fallback) {
		CallableRequestBuilder<OUTPUT, API, RuntimeException> result = new CallableRequestBuilder<>(engine, serviceDetails);
		result.name = name;
		result.timeoutMillis = timeoutMillis;
		result.callable = callable;
		result.fallback = fallback;
		return result;
	}

	public CallableRequest<OUTPUT, ERROR> build() {
		return new ImmutableCallableRequest<OUTPUT, API, ERROR>(name, engine, serviceDetails, timeoutMillis, callable, fallback);
	}

}

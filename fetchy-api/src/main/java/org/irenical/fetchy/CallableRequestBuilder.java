package org.irenical.fetchy;

public class CallableRequestBuilder<OUTPUT, API, ERROR extends Exception> {

	private Fetchy fetchy;

	private String serviceId;

	private Long timeoutMillis;

	private Call<OUTPUT, API, ERROR> callable;

	private CallFallback<OUTPUT> fallback;

	public CallableRequestBuilder(Fetchy fetchy) {
		this.fetchy = fetchy;
	}

	public CallableRequestBuilder<OUTPUT, API, ERROR> service(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}

	public CallableRequestBuilder<OUTPUT, API, ERROR> timeout(Long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
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
		return new ImmutableCallableRequest<>(fetchy, serviceId, timeoutMillis, callable, fallback);
	}

}

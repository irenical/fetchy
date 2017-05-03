package org.irenical.fetchy;

import java.util.concurrent.Callable;

public class ImmutableCallableRequest<OUTPUT, API, ERROR extends Exception> extends ImmutableRequest<OUTPUT, API, ERROR>
		implements CallableRequest<OUTPUT, ERROR> {

	private Call<OUTPUT, API, ERROR> callable;

	private CallFallback<OUTPUT> fallback;

	public ImmutableCallableRequest(String name, Fetchy fetchy, String serviceId, Integer timeoutMillis,
			Call<OUTPUT, API, ERROR> callable, CallFallback<OUTPUT> fallback) {
		super(name, fetchy, serviceId, timeoutMillis);
		this.callable = callable;
		this.fallback = fallback;
	}

	@Override
	public OUTPUT execute() throws ERROR {
		return request();
	}

	@Override
	protected Callable<OUTPUT> getCallable(API api) {
		return () -> callable.call(api);
	}

	@Override
	protected Callable<OUTPUT> getCallableFallback(Throwable error) {
		return fallback == null ? null : () -> fallback.fallback(error);
	}

}

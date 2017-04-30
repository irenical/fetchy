package org.irenical.fetchy;

import java.net.URI;

public class ImmutableCallableRequest<OUTPUT, API, ERROR extends Exception> extends ImmutableRequest
		implements CallableRequest<OUTPUT, ERROR> {

	private Call<OUTPUT, API, ERROR> callable;

	private CallFallback<OUTPUT> fallback;

	public ImmutableCallableRequest(Fetchy fetchy, String serviceId, Long timeoutMillis,
			Call<OUTPUT, API, ERROR> callable, CallFallback<OUTPUT> fallback) {
		super(fetchy, serviceId, timeoutMillis);
		this.callable = callable;
		this.fallback = fallback;
	}

	@Override
	public OUTPUT execute() throws ERROR {
		// TODO, try to reuse code at
		// org.irenical.fetchy.ImmutableRunnableRequest
		try {
			URI node = attemptDiscover();
			API api = fetchy.connect(serviceId, node);
			return callable.call(api);
		} catch (Exception e) {
			if (fallback != null) {
				return fallback.fallback(e);
			} else if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw (ERROR) e;
			}
		}
	}

}

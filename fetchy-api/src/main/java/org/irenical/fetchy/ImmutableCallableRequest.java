package org.irenical.fetchy;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ImmutableCallableRequest<OUTPUT, API, ERROR extends Exception> extends ImmutableRequest
		implements CallableRequest<OUTPUT, ERROR> {

	private Call<OUTPUT, API, ERROR> callable;

	private CallFallback<OUTPUT> fallback;

	public ImmutableCallableRequest(Fetchy fetchy, String serviceId, Integer timeoutMillis,
			Call<OUTPUT, API, ERROR> callable, CallFallback<OUTPUT> fallback) {
		super(fetchy, serviceId, timeoutMillis);
		this.callable = callable;
		this.fallback = fallback;
	}

	@Override
	public OUTPUT execute() throws ERROR {
		// TODO, try to reuse code at
		// org.irenical.fetchy.ImmutableRunnableRequest
		if (timeoutMillis != null && timeoutMillis > 0) {
			Future<OUTPUT> future = fetchy.getExecutorService().submit(() -> {
				return innerExecute();
			});
			try {
				return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				if (fallback != null) {
					return fallback.fallback(e);
				}
				if (e instanceof ExecutionException) {
					Throwable cause = e.getCause();
					if (cause instanceof RuntimeException) {
						throw (RuntimeException) e;
					} else {
						throw (ERROR) cause;
					}
				} else if (e instanceof TimeoutException) {
					throw new RequestTimeoutException("Timeout on service call", e);
				} else {
					throw new RequestException("Error on service call", e);
				}
			}
		} else {
			return innerExecute();
		}
	}

	private OUTPUT innerExecute() throws ERROR {
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

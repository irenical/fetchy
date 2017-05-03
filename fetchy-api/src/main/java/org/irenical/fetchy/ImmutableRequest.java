package org.irenical.fetchy;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ImmutableRequest<OUTPUT, API, ERROR extends Exception> {

	private static final Logger LOG = LoggerFactory.getLogger(ImmutableRequest.class);

	protected final String name;

	protected final Fetchy fetchy;

	protected final String serviceId;

	protected final Integer timeoutMillis;

	public ImmutableRequest(String name, Fetchy fetchy, String serviceId, Integer timeoutMillis) {
		this.name = name == null ? "request@" + serviceId : name;
		this.fetchy = fetchy;
		this.serviceId = serviceId;
		this.timeoutMillis = timeoutMillis;
	}

	private Throwable determineError(Exception e) {
		Throwable error;
		if (e instanceof ExecutionException) {
			error = e.getCause();
		} else if (e instanceof TimeoutException) {
			error = new RequestTimeoutException("Timeout on service run", e);
		} else if (e instanceof InterruptedException || e instanceof CancellationException) {
			error = new RequestAbortException("Error on service run", e);
		} else {
			error = e;
		}
		return error;
	}

	@SuppressWarnings("unchecked")
	private void throwError(Throwable error) throws ERROR {
		if (error instanceof RuntimeException) {
			throw (RuntimeException) error;
		} else {
			throw (ERROR) error;
		}
	}

	private URI attemptDiscover() {
		Discoverer disco = fetchy.getServiceDiscoverer(serviceId);
		if (disco != null) {
			List<URI> uris = disco.discover(serviceId);
			Balancer bal = fetchy.getServiceBalancer(serviceId);
			if (bal != null) {
				return bal.balance(uris);
			} else if (uris != null && !uris.isEmpty()) {
				return uris.get(0);
			}
		}
		return null;
	}

	protected OUTPUT request() throws ERROR {
		long start = System.nanoTime();
		URI node = null;
		OUTPUT result = null;
		Throwable error = null;
		try {
			node = attemptDiscover();
			API api = fetchy.connect(serviceId, node);
			Callable<OUTPUT> callable = getCallable(api);
			if (timeoutMillis != null && timeoutMillis > 0) {
				Future<OUTPUT> future = fetchy.getExecutorService().submit(callable);
				result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
			} else {
				result = callable.call();
			}
		} catch (Exception e) {
			boolean raise = true;
			error = determineError(e);
			Callable<OUTPUT> fallback = getCallableFallback(error);
			if (fallback != null) {
				try {
					result = fallback.call();
					raise = false;
				} catch (Exception fallbackError) {
					LOG.error("Error attempting to run fallback method on request '" + name + "', service '" + serviceId
							+ "'", e);
				}
			}
			if (raise) {
				throwError(error);
			}
		} finally {
			fetchy.fireEvent(name, serviceId, node, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start), error);
		}
		return result;
	}

	protected abstract Callable<OUTPUT> getCallable(API api);

	protected abstract Callable<OUTPUT> getCallableFallback(Throwable error);

}

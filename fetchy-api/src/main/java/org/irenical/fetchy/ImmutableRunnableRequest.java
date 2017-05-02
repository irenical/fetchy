package org.irenical.fetchy;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ImmutableRunnableRequest<API, ERROR extends Exception> extends ImmutableRequest
		implements RunnableRequest<ERROR> {

	private Run<API, ERROR> runnable;

	private RunFallback fallback;

	public ImmutableRunnableRequest(Fetchy fetchy, String serviceId, Integer timeoutMillis, Run<API, ERROR> runnable,
			RunFallback fallback) {
		super(fetchy, serviceId, timeoutMillis);
		this.runnable = runnable;
		this.fallback = fallback;
	}

	@Override
	public void execute() throws ERROR {
		// TODO, try to reuse code at
		// org.irenical.fetchy.ImmutableCallableRequest
		if (timeoutMillis != null && timeoutMillis > 0) {
			Future<?> future = fetchy.getExecutorService().submit(() -> {
				try {
					innerExecute();
				} catch (RuntimeException e) {
					throw e;
				} catch (Exception e) {
					throw new RunException(e);
				}
			});
			try {
				future.get(timeoutMillis, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				if (fallback != null) {
					fallback.fallback(e);
					return;
				}
				if(e instanceof RunException) {
					throw (ERROR) e.getCause();
				}
				if (e instanceof ExecutionException) {
					Throwable cause = e.getCause();
					if (cause instanceof RuntimeException) {
						throw (RuntimeException) e;
					} else {
						throw (ERROR) cause;
					}
				} else if (e instanceof TimeoutException) {
					throw new RequestTimeoutException("Timeout on service run", e);
				} else {
					throw new RequestException("Error on service run", e);
				}
			}
		} else {
			innerExecute();
		}
	}

	private void innerExecute() throws ERROR {
		try {
			URI node = attemptDiscover();
			API api = fetchy.connect(serviceId, node);
			runnable.run(api);
		} catch (Exception e) {
			if (fallback != null) {
				fallback.fallback(e);
			} else if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw (ERROR) e;
			}
		}
	}

}

package org.irenical.fetchy;

import java.net.URI;

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

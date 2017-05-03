package org.irenical.fetchy;

import java.util.concurrent.Callable;

public class ImmutableRunnableRequest<API, ERROR extends Exception> extends ImmutableRequest<Void, API, ERROR>
		implements RunnableRequest<ERROR> {

	private Run<API, ERROR> runnable;

	private RunFallback fallback;

	public ImmutableRunnableRequest(String name, Fetchy fetchy, String serviceId, Integer timeoutMillis,
			Run<API, ERROR> runnable, RunFallback fallback) {
		super(name, fetchy, serviceId, timeoutMillis);
		this.runnable = runnable;
		this.fallback = fallback;
	}

	@Override
	public void execute() throws ERROR {
		request();
	}

	@Override
	protected Callable<Void> getCallable(API api) {
		return () -> {
			runnable.run(api);
			return null;
		};
	}

	@Override
	protected Callable<Void> getCallableFallback(Throwable error) {
		return fallback == null ? null : () -> {
			fallback.fallback(error);
			return null;
		};
	}

}

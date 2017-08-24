package org.irenical.fetchy.request;

import org.irenical.fetchy.engine.FetchyEngine;

import java.util.concurrent.Callable;

public class ImmutableCallableRequest<OUTPUT, API, ERROR extends Exception> extends ImmutableRequest<OUTPUT, API, ERROR>
        implements CallableRequest<OUTPUT, ERROR> {

    private Call<OUTPUT, API, ?> callable;

    private CallFallback<OUTPUT> fallback;

    public ImmutableCallableRequest(String name, FetchyEngine engine, RequestServiceDetails<API> service, Integer timeoutMillis,
                                    Call<OUTPUT, API, ?> callable, CallFallback<OUTPUT> fallback) {
        super(name, engine, service, timeoutMillis);
        this.callable = callable;
		this.fallback = fallback;
	}

    @Override
    public OUTPUT execute() throws ERROR {
        return request();
    }

    @Override
    public Callable<OUTPUT> getCallable(API api) {
        return () -> callable.call(api);
    }

    @Override
    public Callable<OUTPUT> getCallableFallback(Throwable error) {
        return fallback == null ? null : () -> fallback.fallback(error);
    }

}

package org.irenical.fetchy.request;

import org.irenical.fetchy.engine.FetchyEngine;

import java.util.concurrent.Callable;

public class ImmutableRunnableRequest<API, ERROR extends Exception> extends ImmutableRequest<Void, API, ERROR>
        implements RunnableRequest<ERROR> {

    private Run<API, ?> runnable;

    private RunFallback fallback;

    public ImmutableRunnableRequest(String name, FetchyEngine engine, RequestServiceDetails<API> service, Integer timeoutMillis,
                                    Run<API, ?> runnable, RunFallback fallback) {
        super(name, engine, service, timeoutMillis);
        this.runnable = runnable;
        this.fallback = fallback;
    }

    @Override
    public void execute() throws ERROR {
        request();
    }

    @Override
    public Callable<Void> getCallable(API api) {
        return () -> {
            runnable.run(api);
            return null;
        };
    }

    @Override
    public Callable<Void> getCallableFallback(Throwable error) {
        return fallback == null ? null : () -> {
            fallback.fallback(error);
            return null;
        };
    }

}

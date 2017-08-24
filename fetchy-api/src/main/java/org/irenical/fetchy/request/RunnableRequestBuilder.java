package org.irenical.fetchy.request;

import org.irenical.fetchy.engine.FetchyEngine;

public class RunnableRequestBuilder<API, ERROR extends Exception> {

    private final FetchyEngine engine;
    
    private final RequestServiceDetails<API> serviceDetails;

    private final String name;
    
    private Integer timeoutMillis;

    private Run<API, ?> runnable;

    private RunFallback fallback;

    public RunnableRequestBuilder(FetchyEngine engine, RequestServiceDetails<API> serviceDetails, String name) {
        this.engine = engine;
        this.serviceDetails = serviceDetails;
        this.name = name;
    }

    public RunnableRequestBuilder<API, ERROR> timeout(Integer timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }
    
    public RunnableRequestBuilder<API, ERROR> runnable(Run<API, ERROR> lambda) {
        this.runnable = lambda;
        return this;
    }

    public RunnableRequestBuilder<API, RuntimeException> fallback(RunFallback fallback) {
        RunnableRequestBuilder<API, RuntimeException> result = new RunnableRequestBuilder<>(engine, serviceDetails, name);
        result.timeoutMillis = timeoutMillis;
        result.runnable = runnable;
        result.fallback = fallback;
        return result;
    }

    public RunnableRequest<ERROR> build() {
        return new ImmutableRunnableRequest<API,ERROR>(name, engine, serviceDetails, timeoutMillis, runnable, fallback);
    }

}

package org.irenical.fetchy.request;

import org.irenical.fetchy.engine.FetchyEngine;

public class RunnableRequestBuilder<API, ERROR extends Exception> {

    private final FetchyEngine engine;
    
    private final RequestServiceDetails<API> serviceDetails;

    private String name;
    
    private Integer timeoutMillis;

    private Run<API, ERROR> runnable;

    private RunFallback fallback;

    public RunnableRequestBuilder(FetchyEngine engine, RequestServiceDetails<API> serviceDetails) {
        this.engine = engine;
        this.serviceDetails = serviceDetails;
    }

    public RunnableRequestBuilder<API, ERROR> timeout(Integer timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }

    public RunnableRequestBuilder<API, ERROR> name(String name) {
        this.name = name;
        return this;
    }

    public RunnableRequestBuilder<API, ERROR> runnable(Run<API, ERROR> lambda) {
        this.runnable = lambda;
        return this;
    }

    public RunnableRequestBuilder<API, ERROR> fallback(RunFallback fallback) {
        this.fallback = fallback;
        return this;
    }

    public RunnableRequest<ERROR> build() {
        return new ImmutableRunnableRequest<>(name, engine, serviceDetails, timeoutMillis, runnable, fallback);
    }

}

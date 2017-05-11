package org.irenical.fetchy.request;

import org.irenical.fetchy.engine.FetchyEngine;

import java.util.concurrent.Callable;

public abstract class ImmutableRequest<OUTPUT, API, ERROR extends Exception> {

    private final String name;
    private final Integer timeoutMillis;

    private final FetchyEngine engine;
    private final CallServiceDetails<API> serviceDetails;

    public ImmutableRequest(String name, FetchyEngine engine, CallServiceDetails<API> serviceDetails, Integer timeoutMillis) {
        this.name = name;
        this.engine = engine;
        this.serviceDetails = serviceDetails;
        this.timeoutMillis = timeoutMillis;
    }

    public String getName() {
        return name;
    }

    public Integer getTimeoutMillis() {
        return timeoutMillis;
    }

    public CallServiceDetails<API> getServiceDetails() {
        return serviceDetails;
    }

    protected OUTPUT request() throws ERROR {
        return engine.request(this);
    }

    public abstract Callable<OUTPUT> getCallable(API api);

    public abstract Callable<OUTPUT> getCallableFallback(Throwable error);
}

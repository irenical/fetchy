package org.irenical.fetchy.request;

import org.irenical.fetchy.engine.FetchyEngine;

public class RequestBuilder<API> {

    private final FetchyEngine engine;

    private CallServiceDetails<API> serviceDetails;

    private String name;

    public RequestBuilder(FetchyEngine engine, CallServiceDetails<API> serviceDetails) {
        this.engine = engine;
        this.serviceDetails = serviceDetails;
    }

    public RequestBuilder<API> name(String name) {
        this.name = name;
        return this;
    }

    public <OUTPUT, ERROR extends Exception> CallableRequestBuilder<OUTPUT, API, ERROR> callable(Call<OUTPUT, API, ERROR> callable) {
        CallableRequestBuilder<OUTPUT, API, ERROR> result = new CallableRequestBuilder<>(engine, serviceDetails);
        result.name(name);
        result.callable(callable);
        return result;
    }

    public <ERROR extends Exception> RunnableRequestBuilder<API, ERROR> runnable(Run<API, ERROR> runnable) {
        RunnableRequestBuilder<API, ERROR> result = new RunnableRequestBuilder<>(engine, serviceDetails);
        result.name(name);
        result.runnable(runnable);
        return result;
    }

}

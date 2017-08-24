package org.irenical.fetchy.request;

import org.irenical.fetchy.engine.FetchyEngine;

public class RequestBuilder<API> {

    private final FetchyEngine engine;

    private final RequestServiceDetails<API> serviceDetails;

    private final String name;

    public RequestBuilder(FetchyEngine engine, RequestServiceDetails<API> serviceDetails, String name) {
        this.engine = engine;
        this.serviceDetails = serviceDetails;
        this.name = name;
    }

    public <OUTPUT, ERROR extends Exception> CallableRequestBuilder<OUTPUT, API, ERROR> callable(Call<OUTPUT, API, ERROR> callable) {
        CallableRequestBuilder<OUTPUT, API, ERROR> result = new CallableRequestBuilder<>(engine, serviceDetails);
        result.name(name);
        result.callable(callable);
        return result;
    }

    public <ERROR extends Exception> RunnableRequestBuilder<API, ERROR> runnable(Run<API, ERROR> runnable) {
        RunnableRequestBuilder<API, ERROR> result = new RunnableRequestBuilder<>(engine, serviceDetails, name);
        result.runnable(runnable);
        return result;
    }

}

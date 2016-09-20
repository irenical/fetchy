package org.irenical.fetchy.executor.retrofit;

import org.irenical.fetchy.service.factory.rest.RESTServiceDiscoveryFactory;

public class RetrofitServiceDiscoveryFactory<IFACE> extends RESTServiceDiscoveryFactory<IFACE> {

    private final String serviceId;

    public RetrofitServiceDiscoveryFactory(Class<IFACE> ifaceClass, String serviceId) {
        super( ifaceClass );
        this.serviceId = serviceId;
    }

    @Override
    protected RetrofitServiceExecutor< IFACE > createServiceExecutor() {
        return new RetrofitServiceExecutor<>( getServiceInterface(), serviceId );
    }

}

package org.irenical.fetchy.executor.retrofit;

import org.irenical.fetchy.service.factory.rest.RESTServiceDiscoveryFactory;

public class RetrofitServiceDiscoveryFactory<IFACE> extends RESTServiceDiscoveryFactory<IFACE> {

    private final Class< IFACE > ifaceClass;

    private final String serviceId;

    public RetrofitServiceDiscoveryFactory(String id, Class<IFACE> ifaceClass, String serviceId) {
        super( id );
        this.ifaceClass = ifaceClass;
        this.serviceId = serviceId;
    }

    @Override
    protected RetrofitServiceExecutor< IFACE > createServiceExecutor() {
        return new RetrofitServiceExecutor<>( ifaceClass, serviceId );
    }

}

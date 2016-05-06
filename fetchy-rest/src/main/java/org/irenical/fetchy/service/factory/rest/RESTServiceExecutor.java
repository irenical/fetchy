package org.irenical.fetchy.service.factory.rest;

import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.service.factory.ServiceDiscoveryExecutor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;


public class RESTServiceExecutor<IFACE> extends ServiceDiscoveryExecutor<IFACE,IFACE> {

    private final Class< IFACE > ifaceClass;

    public RESTServiceExecutor(Class< IFACE > ifaceClass, String serviceId) {
        super( serviceId );
        this.ifaceClass = ifaceClass;
    }

    @Override
    protected IFACE newInstance(ServiceNode serviceNode) throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( serviceNode.getAddress() )
                .addCallAdapterFactory( RxJavaCallAdapterFactory.create() )
                .addConverterFactory( MoshiConverterFactory.create() )
                .build();

        return retrofit.create( ifaceClass );
    }

    @Override
    protected void onBeforeExecute(IFACE iface) {
    }

    @Override
    protected void onAfterExecute(IFACE iface) {
    }

}

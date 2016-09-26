package org.irenical.fetchy.executor.retrofit;

import okhttp3.OkHttpClient;
import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.service.factory.ServiceDiscoveryExecutor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;


public class RetrofitServiceExecutor<IFACE> extends ServiceDiscoveryExecutor<IFACE,IFACE> {

    private final Class< IFACE > ifaceClass;

    private OkHttpClient httpClient;


    public RetrofitServiceExecutor(Class< IFACE > ifaceClass, String serviceId) {
        super( serviceId );
        this.ifaceClass = ifaceClass;
    }

    @Override
    protected IFACE newInstance(ServiceNode serviceNode) throws Exception {

        if ( httpClient == null ) {
            httpClient = new OkHttpClient();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( serviceNode.getAddress() )
                .client( httpClient )
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


    public void setHttpClient( OkHttpClient httpClient ) {
        this.httpClient = httpClient;
    }

}

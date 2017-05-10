package org.irenical.fetchy.connector.retrofit;

import okhttp3.OkHttpClient;
import org.irenical.fetchy.Node;
import org.irenical.fetchy.connector.ConnectException;
import org.irenical.fetchy.connector.Connector;
import org.irenical.fetchy.connector.Stub;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;


public class RetrofitConnector<IFACE> implements Connector<IFACE> {

  private final Class<IFACE> ifaceClass;

  private OkHttpClient httpClient;

  public RetrofitConnector(Class<IFACE> ifaceClass) {
    this.ifaceClass = ifaceClass;
  }

  public RetrofitConnector(Class<IFACE> ifaceClass, OkHttpClient httpClient) {
    this.ifaceClass = ifaceClass;
    this.httpClient = httpClient;
  }

  @Override
  public Stub<IFACE> connect(Node node) throws ConnectException {

    if (httpClient == null) {
      httpClient = new OkHttpClient();
    }

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(node.getAddress())
        .client(httpClient)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .build();

    return () -> retrofit.create(ifaceClass);
  }

  public void setHttpClient(OkHttpClient httpClient) {
    this.httpClient = httpClient;
  }
}

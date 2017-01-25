package org.irenical.fetchy.service.factory.grpc;

import io.grpc.Channel;
import io.grpc.stub.AbstractStub;
import org.irenical.fetchy.service.Stub;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;

import java.util.function.Function;

public class GrpcServiceDiscoveryFactory<IFACE extends AbstractStub<?>> extends ServiceDiscoveryFactory<IFACE> {

  private final String serviceId;
  private Function<Channel, IFACE> stubGenerator;

  private boolean usePlaintext = false;

  public GrpcServiceDiscoveryFactory(String id, Function<Channel, IFACE> stubGenerator, String serviceId) {
    this( id, stubGenerator, serviceId, false );
  }

  public GrpcServiceDiscoveryFactory(String id, Function<Channel, IFACE> stubGenerator, String serviceId, boolean usePlaintext ) {
    super(id);
    this.stubGenerator = stubGenerator;
    this.serviceId = serviceId;
    this.usePlaintext = usePlaintext;
  }

  @Override
  public Stub<IFACE> createService() {
    GrpcServiceExecutor<IFACE> serviceExecutor = new GrpcServiceExecutor<>(serviceId, stubGenerator, usePlaintext);

    serviceExecutor.setServiceNodeDiscovery(getServiceNodeDiscovery());
    serviceExecutor.setServiceNodeBalancer(getServiceNodeBalancer());

    return serviceExecutor;
  }

}

package org.irenical.fetchy.service.factory.grpc;

import java.util.function.Function;

import org.irenical.fetchy.service.Stub;
import org.irenical.fetchy.service.factory.ServiceDiscoveryFactory;

import io.grpc.Channel;
import io.grpc.stub.AbstractStub;

public class GrpcServiceDiscoveryFactory<IFACE extends AbstractStub<?>> extends ServiceDiscoveryFactory<IFACE> {

  private final String serviceId;
  private Function<Channel, IFACE> stubGenerator;

  public GrpcServiceDiscoveryFactory(String id, Function<Channel, IFACE> stubGenerator, String serviceId) {
    super(id);
    this.stubGenerator = stubGenerator;
    this.serviceId = serviceId;
  }

  @Override
  public Stub<IFACE> createService() {
    GrpcServiceExecutor<IFACE> serviceExecutor = new GrpcServiceExecutor<>(serviceId, stubGenerator);

    serviceExecutor.setServiceNodeDiscovery(getServiceNodeDiscovery());
    serviceExecutor.setServiceNodeBalancer(getServiceNodeBalancer());

    return serviceExecutor;
  }

}

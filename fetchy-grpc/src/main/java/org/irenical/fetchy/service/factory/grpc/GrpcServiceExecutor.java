package org.irenical.fetchy.service.factory.grpc;

import java.util.function.Function;

import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.service.factory.ServiceDiscoveryExecutor;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;

public class GrpcServiceExecutor<IFACE extends AbstractStub<?>> extends ServiceDiscoveryExecutor<IFACE, IFACE> {

  private Function<Channel, IFACE> stubGenerator;

  public GrpcServiceExecutor(String serviceId, Function<Channel, IFACE> stubGenerator) {
    super(serviceId);
    this.stubGenerator = stubGenerator;
  }

  @Override
  protected IFACE newInstance(ServiceNode serviceNode) throws Exception {
    ManagedChannel channel = ManagedChannelBuilder.forAddress(serviceNode.getAddress(), serviceNode.getPort()).build();
    return stubGenerator.apply(channel);
  }

  @Override
  protected void onBeforeExecute(IFACE client) {
  }

  @Override
  protected void onAfterExecute(IFACE client) {
    ManagedChannel channel = (ManagedChannel) ((AbstractStub<?>) client).getChannel();
    channel.shutdown();
  }

}

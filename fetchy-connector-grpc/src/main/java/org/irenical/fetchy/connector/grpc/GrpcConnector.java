package org.irenical.fetchy.connector.grpc;

import java.util.function.Function;

import org.irenical.fetchy.Node;
import org.irenical.fetchy.connector.ConnectException;
import org.irenical.fetchy.connector.Connector;
import org.irenical.fetchy.connector.Stub;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;

public class GrpcConnector<IFACE extends AbstractStub<?>> implements Connector<IFACE> {

  private Function<Channel, IFACE> stubGenerator;

  private boolean usePlaintext = false;

  public GrpcConnector(Function<Channel, IFACE> stubGenerator) {
    this(stubGenerator, false);
  }

  public GrpcConnector(Function<Channel, IFACE> stubGenerator, boolean usePlaintext) {
    this.stubGenerator = stubGenerator;
    this.usePlaintext = usePlaintext;
  }

  @Override
  public Stub<IFACE> connect(Node node) throws ConnectException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress(node.getAddress(), node.getPort())
        .usePlaintext(usePlaintext)
        .build();

    IFACE client = stubGenerator.apply(channel);

    return new Stub<IFACE>() {
      @Override
      public IFACE get() {
        return client;
      }

      @Override
      public void onAfterExecute() {
        channel.shutdown();
      }
    };
  }
}

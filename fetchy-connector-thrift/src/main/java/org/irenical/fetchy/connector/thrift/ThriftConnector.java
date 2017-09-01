package org.irenical.fetchy.connector.thrift;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.irenical.fetchy.Node;
import org.irenical.fetchy.connector.ConnectException;
import org.irenical.fetchy.connector.Connector;
import org.irenical.fetchy.connector.Stub;

public class ThriftConnector<IFACE, CLIENT extends IFACE> implements Connector<CLIENT> {

  private static final Function<Node, TTransport> defaultTransportBuilder = node -> new TFramedTransport(
      new TSocket(node.getAddress(), node.getPort()));

  private static final Function<TTransport, TProtocol> defaultProtocolBuilder = transport -> new TBinaryProtocol(
      transport);

  private final Function<TProtocol, CLIENT> defaultClientBuilder;

  private Function<Node, TTransport> transportBuilder;

  private Function<TTransport, TProtocol> protocolBuilder;

  private Function<TProtocol, CLIENT> clientBuilder;

  public ThriftConnector(Class<CLIENT> clientType) {
    this(clientType, null, null, null);
  }

  public ThriftConnector(Class<CLIENT> clientType, Function<Node, TTransport> transportBuilder,
      Function<TTransport, TProtocol> protocolBuilder, Function<TProtocol, CLIENT> clientBuilder) {
    this.defaultClientBuilder = createDefaultClientBuilder(clientType);
    this.transportBuilder = transportBuilder;
    this.protocolBuilder = protocolBuilder;
    this.clientBuilder = clientBuilder;
  }

  private static <CLIENT> Function<TProtocol, CLIENT> createDefaultClientBuilder(Class<CLIENT> clientType) {
    return protocol -> {
      try {
        return clientType.getConstructor(TProtocol.class).newInstance(protocol);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
          | NoSuchMethodException | SecurityException e) {
        throw new ConnectException("Error running default client builder", e);
      }
    };
  }

  public ThriftConnector<IFACE, CLIENT> withTransportBuilder(Function<Node, TTransport> transportBuilder) {
    this.transportBuilder = transportBuilder;
    return this;
  }

  public Function<Node, TTransport> getTransportBuilder() {
    return transportBuilder;
  }

  public ThriftConnector<IFACE, CLIENT> withProtocolBuilder(Function<TTransport, TProtocol> protocolBuilder) {
    this.protocolBuilder = protocolBuilder;
    return this;
  }

  public Function<TTransport, TProtocol> getProtocolBuilder() {
    return protocolBuilder;
  }

  public ThriftConnector<IFACE, CLIENT> withClientBuilder(Function<TProtocol, CLIENT> clientBuilder) {
    this.clientBuilder = clientBuilder;
    return this;
  }

  public Function<TProtocol, CLIENT> getClientBuilder() {
    return clientBuilder;
  }

  @Override
  public Stub<CLIENT> connect(Node node) throws ConnectException {
    try {
      TTransport tTransport = transportBuilder == null ? defaultTransportBuilder.apply(node) : transportBuilder.apply(node);
      TProtocol protocol = protocolBuilder == null ? defaultProtocolBuilder.apply(tTransport) : protocolBuilder.apply(tTransport);
      CLIENT client = clientBuilder == null ? defaultClientBuilder.apply(protocol) : clientBuilder.apply(protocol);
      
      return new Stub<CLIENT>() {
        @Override
        public CLIENT get() {
          return client;
        }

        @Override
        public void onBeforeExecute() {
          try {
            open(client);
          } catch (TTransportException e) {
            throw new ConnectException(e.getLocalizedMessage(), e);
          }
        }

        @Override
        public void onAfterExecute() {
          close(client);
        }
      };
    } catch (Exception e) {
      throw (e instanceof ConnectException) ? (ConnectException) e : new ConnectException(e.getLocalizedMessage(), e);
    }
  }

  private void open(CLIENT clientInstance) throws TTransportException {
    if (clientInstance != null) {
      open(((TServiceClient) clientInstance).getInputProtocol());
      open(((TServiceClient) clientInstance).getOutputProtocol());
    }
  }

  private void close(CLIENT clientInstance) {
    if (clientInstance != null) {
      close(((TServiceClient) clientInstance).getInputProtocol());
      close(((TServiceClient) clientInstance).getOutputProtocol());
    }
  }

  private void open(TProtocol protocol) throws TTransportException {
    if (protocol != null) {
      TTransport transport = protocol.getTransport();
      if (transport != null && !transport.isOpen()) {
        transport.open();
      }
    }
  }

  private void close(TProtocol protocol) {
    if (protocol != null) {
      TTransport transport = protocol.getTransport();
      if (transport != null && transport.isOpen()) {
        transport.close();
      }
    }
  }
}

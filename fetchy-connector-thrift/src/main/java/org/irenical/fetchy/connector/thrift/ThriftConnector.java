package org.irenical.fetchy.connector.thrift;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ThriftConnector<IFACE, CLIENT extends IFACE> implements Connector<CLIENT> {

  private final Class<CLIENT> clientType;

  public ThriftConnector(Class<CLIENT> clientType) {
    this.clientType = clientType;
  }

  @Override
  public Stub<CLIENT> connect(Node node) throws ConnectException {
    try {
      TTransport tTransport = new TFramedTransport(new TSocket(node.getAddress(), node.getPort()));
      TProtocol protocol = new TBinaryProtocol(tTransport);
      Constructor<CLIENT> constructor = clientType.getConstructor(TProtocol.class);
      CLIENT client = constructor.newInstance(protocol);

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
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
      throw new ConnectException(e.getLocalizedMessage(), e);
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

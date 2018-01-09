package org.irenical.fetchy.connector.jersey2;

import org.irenical.fetchy.Node;
import org.irenical.fetchy.connector.ConnectException;
import org.irenical.fetchy.connector.Connector;
import org.irenical.fetchy.connector.Stub;
import org.irenical.lifecycle.LifeCycle;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.util.function.Consumer;

public class Jersey2Connector implements Connector<WebTarget>, LifeCycle {

  static final String DEFAULT_SCHEME = "http";

  private Client client;

  private boolean isRunning = false;

  private Consumer<ClientBuilder> clientConfigurator;

  public Jersey2Connector() {
    this( null );
  }

  public Jersey2Connector(Consumer<ClientBuilder> clientConfigurator) {
    this.clientConfigurator = clientConfigurator;
  }

  public Jersey2Connector withClientConfigurator(Consumer<ClientBuilder> clientConfigurator) {
    this.clientConfigurator = clientConfigurator;
    return this;
  }

  @Override
  public Stub<WebTarget> connect(Node node) throws ConnectException {
    if (client == null) {
      ClientBuilder builder = ClientBuilder.newBuilder();

      if (clientConfigurator != null) {
        clientConfigurator.accept( builder );
      }

      client = builder.build();
    }

    UriBuilder uriBuilder;

    if (node.getAddress().matches("(\\w+)?\\:?\\/\\/.*")) {
      uriBuilder = UriBuilder.fromUri(node.getAddress());
    } else {
      uriBuilder = UriBuilder.fromUri(DEFAULT_SCHEME + "://" + node.getAddress());
    }

    if (node.getPort() != null) {
      uriBuilder = uriBuilder.port(node.getPort());
    }

    final WebTarget target = client.target(uriBuilder.build());

    return () -> target;
  }

  @Override
  public <ERROR extends Exception> void start() throws ERROR {
    isRunning = true;
  }

  @Override
  public <ERROR extends Exception> void stop() throws ERROR {
    // FIXME: close the client on fetchy shutdown instead of implementing LifeCycle directly
    disposeClient();

    isRunning = false;
  }

  @Override
  public <ERROR extends Exception> boolean isRunning() throws ERROR {
    return isRunning;
  }

  private void disposeClient() {
    if (client != null) {
      client.close();
      client = null;
    }
  }
}

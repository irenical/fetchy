package org.irenical.fetchy.connector.jersey2;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.irenical.fetchy.Node;
import org.irenical.fetchy.connector.ConnectException;
import org.irenical.fetchy.connector.Connector;
import org.irenical.fetchy.connector.Stub;
import org.irenical.lifecycle.LifeCycle;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

public class Jersey2Connector implements Connector<WebTarget>, LifeCycle {

  private Client client;

  private boolean isRunning = false;

  @Override
  public Stub<WebTarget> connect(Node node) throws ConnectException {
    if (client == null) {
      client = ClientBuilder.newBuilder()
          .register(JacksonFeature.class)
          .build();
    }

    UriBuilder uriBuilder = UriBuilder.fromUri(node.getAddress());

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

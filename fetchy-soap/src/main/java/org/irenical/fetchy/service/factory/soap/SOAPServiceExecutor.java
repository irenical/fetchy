package org.irenical.fetchy.service.factory.soap;

import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.service.factory.ServiceDiscoveryExecutor;

import javax.xml.ws.Service;
import java.net.URI;

public class SOAPServiceExecutor<PORT, ENDPOINT extends Service> extends ServiceDiscoveryExecutor<PORT, PORT> {

  private final Class<PORT> portClass;

  private final Class<ENDPOINT> endpointClass;

  private String address;

  private PORT port;

  public SOAPServiceExecutor(Class<PORT> portClass, Class<ENDPOINT> endpointClass, String serviceId) {
    super(serviceId);

    this.portClass = portClass;
    this.endpointClass = endpointClass;
  }

  @Override
  protected PORT newInstance(ServiceNode serviceNode) throws Exception {
    if (address == null || !serviceNode.getAddress().equals(address) || port == null) {
      address = serviceNode.getAddress();

      ServiceClient<ENDPOINT, PORT> serviceClient = new ServiceClient<>(endpointClass, portClass,
          URI.create(serviceNode.getAddress()));
      port = serviceClient.getPort();
    }
    return port;
  }

  @Override
  protected void onBeforeExecute(PORT port) {

  }

  @Override
  protected void onAfterExecute(PORT port) {

  }

}

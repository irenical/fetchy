package org.irenical.fetchy.service.factory.soap;

import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.service.factory.ServiceDiscoveryExecutor;
import org.irenical.fetchy.service.factory.soap.filter.ServiceClientFilter;

import javax.xml.ws.Service;
import java.net.URI;

public class SOAPServiceExecutor< ENDPOINT extends Service, PORT > extends ServiceDiscoveryExecutor<PORT, PORT> {

  private final Class< ENDPOINT > endpointClass;

  private final Class< PORT > portClass;

  private final ServiceClientFilter[] filters;


  private String address;

  private PORT port;


  public SOAPServiceExecutor( Class<ENDPOINT> endpointClass, Class<PORT> portClass, String serviceId ) {
    this( endpointClass, portClass, serviceId, null );
  }

  public SOAPServiceExecutor( Class<ENDPOINT> endpointClass, Class<PORT> portClass, String serviceId,
                              ServiceClientFilter[] filters ) {
    super(serviceId);

    this.endpointClass = endpointClass;
    this.portClass = portClass;
    this.filters = filters;
  }

  @Override
  protected PORT newInstance( ServiceNode serviceNode ) throws Exception {
    if (address == null || !serviceNode.getAddress().equals(address) || port == null) {
      address = serviceNode.getAddress();

      ServiceClient<ENDPOINT, PORT> serviceClient = new ServiceClient<>( endpointClass, portClass,
          URI.create(serviceNode.getAddress()), filters );
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

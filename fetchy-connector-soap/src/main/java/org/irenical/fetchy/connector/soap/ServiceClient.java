package org.irenical.fetchy.connector.soap;

import org.irenical.fetchy.connector.soap.filter.ServiceClientFilter;
import org.irenical.fetchy.connector.soap.filter.ServiceClientPortFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Class that helps you initialize JAX-WS services and ports.
 *
 * Enables two main features, namely:
 *
 * <ul>
 * <li>Applying a set of "filters" to the endpoint and retrieved ports; these are classes that perform arbitrary actions
 * on the endpoint and port objects upon their initialization. There are various filters available for commonly used
 * features, such as enabling logging or setting options for proper MTOM/XOP transmission.
 * Filters must extend from {@link ServiceClientFilter}.</li>
 * </ul>
 *
 * @param <ENDPOINT> the SOAP service endpoint to use with this client (must extend {@link Service})
 * @param <PORT>     the class that acts as an entry point for the port methods (a JAX-WS client proxy)
 *
 * @see ServiceClientFilter
 */
public class ServiceClient<ENDPOINT extends Service, PORT> {
    
    private final Logger LOG = LoggerFactory.getLogger( ServiceClient.class );


    private final Class<ENDPOINT> endpointClass;

    private final Class<PORT> portClass;

    private final URI serviceUrl;

    private final List<WebServiceFeature> webServiceFeatureList = new LinkedList<>();

    private final List< ServiceClientFilter > serviceClientFilters;


    private ENDPOINT endpointSOAPService;

    private List<Handler> handlers = new LinkedList<>();

    private HandlerResolver handlerResolver = paramPortInfo -> handlers;


    ServiceClient(Class<ENDPOINT> endpointClass, Class<PORT> portClass, URI serviceUrl ) {
        this( endpointClass, portClass, serviceUrl, null );
    }

    ServiceClient(Class<ENDPOINT> endpointClass, Class<PORT> portClass, URI serviceUrl, ServiceClientFilter[] filters) {
        Objects.requireNonNull( endpointClass );
        Objects.requireNonNull( portClass );
        Objects.requireNonNull( serviceUrl );

        this.endpointClass = endpointClass;
        this.portClass = portClass;
        this.serviceUrl = serviceUrl;
        this.serviceClientFilters = filters == null ? Collections.emptyList() : Arrays.asList( filters );

        initEndpoint();
    }

    private void initEndpoint() {
        try {
            URL url = serviceUrl.toURL();
            endpointSOAPService = endpointClass.getConstructor( URL.class ).newInstance( url );
        } catch( Exception e ) {
            throw new RuntimeException("Error creating a new instance of SOAP endpoint: " + endpointClass.getName() + " for url: " + serviceUrl, e);
        }

        // init client filters, they do things such as adding SOAP handlers
        for ( ServiceClientFilter filter : serviceClientFilters ) {
            filter.init(this);
            LOG.info("Loaded service client filter " + filter.getClass().getName());
        }

        if ( ! handlers.isEmpty() ) {
            setSOAPHandlers();
        }
    }

    private void setSOAPHandlers() {
        endpointSOAPService.setHandlerResolver(handlerResolver);
    }

    /**
     * Add a SOAP {@link Handler} directly.
     *
     * Using this method directly is discouraged and it should be used primarily from within
     * {@link ServiceClientFilter}s.
     *
     * @param handler the SOAP handler to be added to the service endpoint
     */
    public void addSOAPHandler(Handler handler) {
        handlers.add(handler);

        setSOAPHandlers();
    }

    /**
     * Add a SOAP {@link WebServiceFeature} directly.
     *
     * Using this method directly is discouraged and it should be used primarily from within
     * {@link ServiceClientFilter}s.
     *
     * @param feature the SOAP WebServiceFeature to be added to the service endpoint
     */
    public void addSOAPFeature(WebServiceFeature feature) {
        webServiceFeatureList.add(feature);
    }

    PORT getPort() {
        return getPort( null );
    }

    PORT getPort( ServiceClientPortFilter[] portFilters ) {
        PORT port = endpointSOAPService.getPort(portClass, webServiceFeatureList.toArray(
                new WebServiceFeature[webServiceFeatureList.size()]));

        for ( ServiceClientFilter filter : serviceClientFilters ) {
            filter.postGetPort(this, port);
        }

        if ( portFilters != null ) {
            for ( ServiceClientPortFilter portFilter : portFilters ) {
                portFilter.postGetPort( port );
            }
        }

        return port;
    }

}

package org.irenical.fetchy.service.factory.soap;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;

import org.irenical.jindy.Config;
import org.irenical.jindy.ConfigFactory;
import org.irenical.jindy.ConfigNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <ul>
 * <li>{@code service/example/url} - URL of the remote WSDL for the service being initialized. String type. Required.</li>
 * <li>{@code service/example/filters} - Comma separated list of filter class names to load. Fully qualified names are
 * required for filters outside the {@code org.irenical.fetchy.service.factory.soap}
 * package. String type. Optional.</li>
 * </ul>
 *
 * @param <ENDPOINT> the SOAP service endpoint to use with this client (must extend {@link Service})
 * @param <PORT>     the class that acts as an entry point for the port methods (a JAX-WS client proxy)
 *
 * @see ServiceClientFilter
 */
public class ServiceClient<ENDPOINT extends Service, PORT> {
    
    private final Logger LOG = LoggerFactory.getLogger(ServiceClient.class);
    
    public static final String DEFAULT_FILTER_PACKAGE = "org.irenical.fetchy.service.factory.soap.";

    private final Class<ENDPOINT> endpointClass;
    private final Class<PORT> portClass;

    private Context jndiContext = null;
    private URI soapURI = null;
    private ENDPOINT endpointSOAPService;
    private List<Handler> handlers = new LinkedList<>();
    private List<WebServiceFeature> webServiceFeatureList = new LinkedList<>();
    private List<ServiceClientFilter> serviceClientFilters = new LinkedList<>();

    private HandlerResolver handlerResolver = paramPortInfo -> handlers;

    public ServiceClient(Class<ENDPOINT> endpointClass, Class<PORT> portClass, URI soapURL, ServiceClientFilter... filters) {
        this.endpointClass = endpointClass;
        this.portClass = portClass;

        this.soapURI = soapURL;
        if(filters != null) this.serviceClientFilters = Arrays.asList(filters);
        initEndpoint();
    }

    public ServiceClient(Class<ENDPOINT> endpointClass, Class<PORT> portClass, String soapURLProperty, String filtersProperty, ServiceClientFilter... filters) {
        this.endpointClass = endpointClass;
        this.portClass = portClass;

        String soapURL;
        Config configs = ConfigFactory.getConfig();
        try {
            soapURL = configs.getMandatoryString(soapURLProperty);
        } catch (ConfigNotFoundException cnfe) {
            throw new ServiceConfigException("SoapURL config not found.", cnfe);
        }

        try {
            String filterConfigs = configs.getMandatoryString(filtersProperty);
            initFiltersFromProperties(filterConfigs);
        } catch (ConfigNotFoundException e) {
            LOG.info("Service Filters config property not found: " + filtersProperty + ". Using fallback filters");
            if(filters != null) {
                this.serviceClientFilters = Arrays.asList(filters);
            } else {
                LOG.info("Service fallback filters not found");
            }
        }

        initEndpoint(soapURL);
    }

    private void initFiltersFromJNDI() {
        initFiltersFromProperties(null);
    }

    private void initFiltersFromProperties(String filterList) {
        if(filterList == null) {
            try {
                if(jndiContext == null) {
                    LOG.info("No JNDI context for searching filters");
                    return;
                }
                filterList = (String) jndiContext.lookup("filters");
            } catch (NamingException e) {
                LOG.info("No client service filter list found in JNDI context /filters for service with endpoint "
                        + endpointClass.getName());
                return;
            }
        }

        for(String filterClassName : filterList.split(",")) {
            if(filterClassName.isEmpty()) continue;

            if(!filterClassName.contains(".")) filterClassName = DEFAULT_FILTER_PACKAGE + filterClassName.trim();
            Class<?> clazz;
            try {
                clazz = Class.forName(filterClassName);
            } catch(ClassNotFoundException e) {
                throw new IllegalArgumentException("Class name " + filterClassName + " given in /filters not found", e);
            }
            if(!ServiceClientFilter.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Given class " + clazz.getName() +
                        " in /filters, but class doesn't extend ServiceClientFilter");
            }

            @SuppressWarnings("unchecked")
            Class<ServiceClientFilter> scfClass = (Class<ServiceClientFilter>) clazz;

            try {
                Constructor<ServiceClientFilter> constructor;
                ServiceClientFilter filter;
                if(jndiContext != null) {
                    constructor = scfClass.getConstructor(Context.class);
                    filter = constructor.newInstance(jndiContext);
                    serviceClientFilters.add(filter);
                } else {
                    constructor = scfClass.getConstructor();
                    filter = constructor.newInstance();
                }
                serviceClientFilters.add(filter);
            } catch(Exception e) {
                throw new IllegalArgumentException("Error initializing " + filterClassName + ": (given in /filters)", e);
            }
        }
    }

    private void initEndpoint() {
        initEndpoint(null);
    }

    private void initEndpoint(String serviceURL) {
        try {
            URL soapURL = (soapURI != null) ? soapURI.toURL() : new URL(serviceURL);
            endpointSOAPService = endpointClass.getConstructor(URL.class).newInstance(soapURL);
        } catch(Exception e) {
            throw new RuntimeException("Error creating a new instance of SOAP endpoint: " + endpointClass.getName(), e);
        }

        // init client filters, they do things such as adding SOAP handlers
        for(ServiceClientFilter filter : serviceClientFilters) {
            filter.init(this);
            LOG.info("Loaded service client filter " + filter.getClass().getName());
        }

        if(!handlers.isEmpty()) setSOAPHandlers();
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

    public ENDPOINT getEndpoint() {
        return endpointSOAPService;
    }

    public PORT getPort(ServiceClientPortFilter ... portFilters) {
        PORT port = getEndpoint().getPort(portClass, webServiceFeatureList.toArray(new WebServiceFeature[webServiceFeatureList.size()]));

        for(ServiceClientFilter filter : serviceClientFilters) {
            filter.postGetPort(this, port);
        }

        if(portFilters != null) {
            for(ServiceClientPortFilter portFilter : portFilters) {
                portFilter.postGetPort(port);
            }
        }

        return port;
    }

    public PORT getPort() {
        return getPort(null);
    }
}

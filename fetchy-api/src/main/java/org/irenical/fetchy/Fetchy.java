package org.irenical.fetchy;

import org.irenical.fetchy.balancer.Balancer;
import org.irenical.fetchy.connector.Connector;
import org.irenical.fetchy.discoverer.Discoverer;
import org.irenical.fetchy.engine.FetchyEngine;
import org.irenical.fetchy.event.FetchyEvent;
import org.irenical.fetchy.request.Call;
import org.irenical.fetchy.request.RequestBuilder;
import org.irenical.fetchy.request.RequestServiceDetails;
import org.irenical.fetchy.request.Run;
import org.irenical.lifecycle.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class Fetchy implements LifeCycle {

    private static final Logger LOG = LoggerFactory.getLogger(Fetchy.class);

    private final Map<String, Discoverer> discos = new ConcurrentHashMap<>();
    private final Map<String, Balancer> bals = new ConcurrentHashMap<>();
    private final Map<String, Connector<?>> cons = new ConcurrentHashMap<>();

    private FetchyEngine engine;

    public Fetchy() {
        engine = new FetchyEngine();
    }

    public Fetchy(ExecutorService executorService) {
        engine = new FetchyEngine(executorService);
    }

    @Override
    public void start() {
        LOG.info("Booting up Fetchy");
        engine.start();
        LOG.info("Fetchy boot up complete");
    }

    @Override
    public void stop() {
        LOG.info("Shutting down Fetchy");
        engine.stop();
        LOG.info("Fetchy shutdown complete");
    }

    @Override
    public <ERROR extends Exception> boolean isRunning() throws ERROR {
        return engine.isRunning();
    }

    public void registerDiscoverer(String serviceId, Discoverer discoverer) {
        LOG.debug("Registering discoverer {} on service {}", discoverer, serviceId);
        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }
        discos.put(serviceId, discoverer);
    }

    public void registerBalancer(String serviceId, Balancer balancer) {
        LOG.debug("Registering balancer {} on service {}", balancer, serviceId);
        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }
        bals.put(serviceId, balancer);
    }

    public void registerConnector(String serviceId, Connector<?> connector) {
        LOG.debug("Registering connector {} on service {}", connector, serviceId);
        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }
        if (connector == null) {
            throw new IllegalArgumentException("Service Connector cannot be null");
        }
        cons.put(serviceId, connector);
    }

    public void register(String serviceId, Discoverer discoverer, Balancer balancer, Connector<?> connector) {
        registerDiscoverer(serviceId, discoverer);
        registerBalancer(serviceId, balancer);
        registerConnector(serviceId, connector);
    }

    public Discoverer getServiceDiscoverer(String serviceId) {
        return discos.get(serviceId);
    }

    public Balancer getServiceBalancer(String serviceId) {
        return bals.get(serviceId);
    }

    @SuppressWarnings("unchecked")
    public <API> Connector<API> getServiceConnector(String serviceId) {
        return (Connector<API>) cons.get(serviceId);
    }

    public String onDiscover(Consumer<FetchyEvent<List<Node>>> listener) {
        return engine.onDiscover(listener);
    }

    public String onBalance(Consumer<FetchyEvent<Node>> listener) {
        return engine.onBalance(listener);
    }

    public <API> String onConnect(Consumer<FetchyEvent<API>> listener) {
        return engine.onConnect(listener);
    }

    public String onRequest(Consumer<FetchyEvent<?>> listener) {
        return engine.onRequest(listener);
    }

    public String onError(Consumer<FetchyEvent<Throwable>> listener) {
        return engine.onError(listener);
    }

    public void removeListener(String listenerId) {
        engine.removeListener(listenerId);
    }

    public <OUTPUT, API, ERROR extends Exception> OUTPUT call(String serviceId, Class<API> apiClass,
                                                              Call<OUTPUT, API, ERROR> call) throws ERROR {
        return call(serviceId, null, apiClass, call);
    }

    public <OUTPUT, API, ERROR extends Exception> OUTPUT call(String serviceId, String requestName, Class<API> apiClass,
                                                              Call<OUTPUT, API, ERROR> call) throws ERROR {
        RequestBuilder<API> rb = createRequest(serviceId, requestName, apiClass);
        return rb.callable(call).build().execute();
    }

    public <API, ERROR extends Exception> void run(String serviceId, Class<API> apiClass, Run<API, ERROR> run)
            throws ERROR {
        run(serviceId, null, apiClass, run);
    }

    public <API, ERROR extends Exception> void run(String serviceId, String requestName, Class<API> apiClass,
                                                   Run<API, ERROR> run) throws ERROR {
        RequestBuilder<API> rb = createRequest(serviceId, requestName, apiClass);
        rb.runnable(run).build().execute();
    }

    public <API> RequestBuilder<API> createRequest(String serviceId, Class<API> apiClass) {
        return createRequest(serviceId, null, apiClass);
    }

    public <API> RequestBuilder<API> createRequest(String serviceId, String requestName, Class<API> apiClass) {
        return new RequestBuilder<API>(engine,
                resolve(serviceId, apiClass),
                requestName == null ? "request@" + serviceId : requestName);
    }

    private <API> RequestServiceDetails<API> resolve(String serviceId, Class<API> apiClass) {
        return new RequestServiceDetails<>(
                serviceId,
                getServiceConnector(serviceId),
                getServiceBalancer(serviceId),
                getServiceDiscoverer(serviceId)
        );
    }

}

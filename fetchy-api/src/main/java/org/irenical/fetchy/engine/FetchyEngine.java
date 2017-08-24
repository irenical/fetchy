package org.irenical.fetchy.engine;

import org.irenical.fetchy.Node;
import org.irenical.fetchy.balancer.Balancer;
import org.irenical.fetchy.connector.Connector;
import org.irenical.fetchy.connector.ConnectorMissingException;
import org.irenical.fetchy.connector.Stub;
import org.irenical.fetchy.discoverer.Discoverer;
import org.irenical.fetchy.event.EventEmitter;
import org.irenical.fetchy.event.FetchyEvent;
import org.irenical.fetchy.request.RequestServiceDetails;
import org.irenical.fetchy.request.ImmutableRequest;
import org.irenical.fetchy.request.RequestAbortException;
import org.irenical.fetchy.request.RequestTimeoutException;
import org.irenical.lifecycle.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class FetchyEngine implements LifeCycle {

    private static final Logger LOG = LoggerFactory.getLogger(FetchyEngine.class);

    static final String EVENT_DISCOVER = "discover";
    static final String EVENT_BALANCE = "balance";
    static final String EVENT_CONNECT = "connect";
    static final String EVENT_REQUEST = "request";
    static final String EVENT_ERROR = "error";

    private final EventEmitter emitter;

    private ExecutorService executorService;

    public FetchyEngine() {
        this(new EventEmitter());
    }

    public FetchyEngine(ExecutorService executorService) {
        this();
        this.executorService = executorService;
    }

    FetchyEngine(EventEmitter emitter) {
        this.emitter = emitter;
    }



    @Override
    public <ERROR extends Exception> void start() throws ERROR {
        // Do nothing
    }

    @Override
    public <ERROR extends Exception> void stop() throws ERROR {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }

        emitter.stop();
    }

    @Override
    public <ERROR extends Exception> boolean isRunning() throws ERROR {
        return true;
    }

    public <OUTPUT, API, ERROR extends Exception> OUTPUT request(ImmutableRequest<OUTPUT, API, ERROR> request) throws ERROR {
        long start = System.nanoTime();
        Node node = null;
        OUTPUT result = null;
        Throwable error = null;

        final RequestServiceDetails<API> service = request.getServiceDetails();

        final String serviceId = service.getServiceId();
        final String name = request.getName();
        final Integer timeoutMillis = request.getTimeoutMillis();

        try {
            node = attemptDiscover(name, service, start);
            Stub<API> api = connect(service, node);
            emitter.fire(EVENT_CONNECT, name, serviceId, node, api.get(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            Callable<OUTPUT> callable = request.getCallable(api.get());
            if (timeoutMillis != null && timeoutMillis > 0) {
                Future<OUTPUT> future = getExecutorService().submit(() -> doRequest(api, callable));
                result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            } else {
                result = doRequest(api, callable);
            }
            emitter.fire(EVENT_REQUEST, name, serviceId, node, null, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
        } catch (Exception e) {
            boolean raise = true;
            error = determineError(e);
            Callable<OUTPUT> fallback = request.getCallableFallback(error);
            if (fallback != null) {
                try {
                    result = fallback.call();
                    raise = false;
                } catch (Exception fallbackError) {
                    LOG.error("Error attempting to run fallback method on request '{}', service '{}'", name, serviceId, fallbackError);
                }
            }
            if (raise) {
                throwError(error);
            }
        } finally {
            if (error != null) {
                emitter.fire(EVENT_ERROR, name, serviceId, node, error, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            }
        }
        return result;
    }

    private <API> Node attemptDiscover(String name, RequestServiceDetails<API> service, long start) {
        String serviceId = service.getServiceId();
        Discoverer disco = service.getDiscoverer();
        if (disco != null) {
            List<Node> nodes = disco.discover(serviceId);
            emitter.fire(EVENT_DISCOVER, name, serviceId, null, nodes, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            Balancer bal = service.getBalancer();
            if (bal != null) {
                Node node = bal.balance(nodes);
                emitter.fire(EVENT_BALANCE, name, serviceId, node, node, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
                return node;
            } else if (nodes != null && !nodes.isEmpty()) {
                return nodes.get(0);
            }
        }
        return null;
    }

    private <API> Stub<API> connect(RequestServiceDetails<API> serviceDetails, Node node) {
        LOG.debug("Connecting service {} to node at {}", serviceDetails.getServiceId(), node);
        Connector<API> connector = serviceDetails.getConnector();
        if (connector == null) {
            throw new ConnectorMissingException("No connector registered for service " + serviceDetails.getServiceId());
        }
        return connector.connect(node);
    }

    private <OUTPUT, API> OUTPUT doRequest(Stub<API> api, Callable<OUTPUT> callable) throws Exception {
        api.onBeforeExecute();

        try {
            return callable.call();
        } finally {
            api.onAfterExecute();
        }
    }

    private Throwable determineError(Exception e) {
        Throwable error;
        if (e instanceof ExecutionException) {
            error = e.getCause();
        } else if (e instanceof TimeoutException) {
            error = new RequestTimeoutException("Timeout on service run", e);
        } else if (e instanceof InterruptedException || e instanceof CancellationException) {
            error = new RequestAbortException("Error on service run", e);
        } else {
            error = e;
        }
        return error;
    }

    @SuppressWarnings("unchecked")
    private <ERROR extends Exception> void throwError(Throwable error) throws ERROR {
        if (error instanceof RuntimeException) {
            throw (RuntimeException) error;
        } else {
            throw (ERROR) error;
        }
    }

    synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }

    public String onDiscover(Consumer<FetchyEvent<List<Node>>> listener) {
        return emitter.addListener(FetchyEngine.EVENT_DISCOVER, listener);
    }

    public String onBalance(Consumer<FetchyEvent<Node>> listener) {
        return emitter.addListener(FetchyEngine.EVENT_BALANCE, listener);
    }

    public <API> String onConnect(Consumer<FetchyEvent<API>> listener) {
        return emitter.addListener(FetchyEngine.EVENT_CONNECT, listener);
    }

    public String onRequest(Consumer<FetchyEvent<?>> listener) {
        return emitter.addListener(FetchyEngine.EVENT_REQUEST, listener);
    }

    public String onError(Consumer<FetchyEvent<Throwable>> listener) {
        return emitter.addListener(FetchyEngine.EVENT_ERROR, listener);
    }

    public void removeListener(String listenerId) {
        if (listenerId == null || listenerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Listener ID cannot be null or empty");
        }

        emitter.removeListener(listenerId);
    }
}

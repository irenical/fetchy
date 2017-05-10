package org.irenical.fetchy.request;

import org.irenical.fetchy.Fetchy;
import org.irenical.fetchy.Node;
import org.irenical.fetchy.balancer.Balancer;
import org.irenical.fetchy.connector.Stub;
import org.irenical.fetchy.discoverer.Discoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ImmutableRequest<OUTPUT, API, ERROR extends Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(ImmutableRequest.class);

    protected final String name;

    protected final Fetchy fetchy;

    protected final String serviceId;

    protected final Integer timeoutMillis;

    public ImmutableRequest(String name, Fetchy fetchy, String serviceId, Integer timeoutMillis) {
        this.name = name;
        this.fetchy = fetchy;
        this.serviceId = serviceId;
        this.timeoutMillis = timeoutMillis;
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
    private void throwError(Throwable error) throws ERROR {
        if (error instanceof RuntimeException) {
            throw (RuntimeException) error;
        } else {
            throw (ERROR) error;
        }
    }

    private Node attemptDiscover(long start) {
        Discoverer disco = fetchy.getServiceDiscoverer(serviceId);
        if (disco != null) {
            List<Node> nodes = disco.discover(serviceId);
            fetchy.fireDiscover(name, serviceId, nodes, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            Balancer bal = fetchy.getServiceBalancer(serviceId);
            if (bal != null) {
                Node node = bal.balance(nodes);
                fetchy.fireBalance(name, serviceId, node, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
                return node;
            } else if (nodes != null && !nodes.isEmpty()) {
                return nodes.get(0);
            }
        }
        return null;
    }

    protected OUTPUT request() throws ERROR {
        long start = System.nanoTime();
        Node node = null;
        OUTPUT result = null;
        Throwable error = null;
        try {
            node = attemptDiscover(start);
            Stub<API> api = fetchy.connect(serviceId, node);
            fetchy.fireConnect(name, serviceId, node, api.get(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            Callable<OUTPUT> callable = getCallable(api.get());
            if (timeoutMillis != null && timeoutMillis > 0) {
                Future<OUTPUT> future = fetchy.getExecutorService().submit(() -> doRequest(api, callable));
                result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            } else {
                result = doRequest(api, callable);
            }
            fetchy.fireRequest(name, serviceId, node, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
        } catch (Exception e) {
            boolean raise = true;
            error = determineError(e);
            Callable<OUTPUT> fallback = getCallableFallback(error);
            if (fallback != null) {
                try {
                    result = fallback.call();
                    raise = false;
                } catch (Exception fallbackError) {
                    LOG.error("Error attempting to run fallback method on request '" + name + "', service '" + serviceId
                            + "'", fallbackError);
                }
            }
            if (raise) {
                throwError(error);
            }
        } finally {
            if (error != null) {
                fetchy.fireError(name, serviceId, node, error, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            }
        }
        return result;
    }

    private OUTPUT doRequest(Stub<API> api, Callable<OUTPUT> callable) throws Exception {
        api.onBeforeExecute();

        try {
            return callable.call();
        } finally {
            api.onAfterExecute();
        }
    }

    protected abstract Callable<OUTPUT> getCallable(API api);

    protected abstract Callable<OUTPUT> getCallableFallback(Throwable error);

}

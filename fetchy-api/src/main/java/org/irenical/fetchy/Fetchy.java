package org.irenical.fetchy;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.irenical.lifecycle.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fetchy implements LifeCycle {

	private static final Logger LOG = LoggerFactory.getLogger(Fetchy.class);

	private static final String EVENT_DISCOVER = "discover";

	private static final String EVENT_BALANCE = "balance";

	private static final String EVENT_CONNECT = "connect";

	private static final String EVENT_REQUEST = "request";

	private static final String EVENT_ERROR = "error";

	private final Map<String, Discoverer> discos = new ConcurrentHashMap<>();

	private final Map<String, Balancer> bals = new ConcurrentHashMap<>();

	private final Map<String, Connector<?>> cons = new ConcurrentHashMap<>();

	private final Map<String, Consumer<FetchyEvent<List<URI>>>> discoverListeners = new ConcurrentHashMap<>();

	private final AtomicInteger discoverListenerId = new AtomicInteger(0);

	private final Map<String, Consumer<FetchyEvent<URI>>> balanceListeners = new ConcurrentHashMap<>();

	private final AtomicInteger balanceListenerId = new AtomicInteger(0);

	private final Map<String, Consumer<FetchyEvent<?>>> connectListeners = new ConcurrentHashMap<>();

	private final AtomicInteger connectListenerId = new AtomicInteger(0);

	private final Map<String, Consumer<FetchyEvent<?>>> requestListeners = new ConcurrentHashMap<>();

	private final AtomicInteger requestListenerId = new AtomicInteger(0);

	private final Map<String, Consumer<FetchyEvent<Throwable>>> errorListeners = new ConcurrentHashMap<>();

	private final AtomicInteger errorListenerId = new AtomicInteger(0);

	private ExecutorService executorService;

	protected synchronized ExecutorService getExecutorService() {
		if (executorService == null) {
			executorService = Executors.newCachedThreadPool();
		}
		return executorService;
	}

	protected void fireDiscover(String name, String serviceId, List<URI> nodes, long elapsedMillis) {
		fire(discoverListeners, name, serviceId, null, nodes, elapsedMillis);
	}

	protected void fireBalance(String name, String serviceId, URI node, long elapsedMillis) {
		fire(balanceListeners, name, serviceId, node, node, elapsedMillis);
	}

	protected void fireConnect(String name, String serviceId, URI node, Object stub, long elapsedMillis) {
		fire(connectListeners, name, serviceId, node, stub, elapsedMillis);
	}

	protected void fireRequest(String name, String serviceId, URI node, long elapsedMillis) {
		fire(requestListeners, name, serviceId, node, null, elapsedMillis);
	}

	protected void fireError(String name, String serviceId, URI node, Throwable error, long elapsedMillis) {
		fire(errorListeners, name, serviceId, node, error, elapsedMillis);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void fire(Map listeners, String name, String serviceId, URI node, Object target, long elapsedMillis) {
		getExecutorService().execute(() -> {
			FetchyEvent<?> event = new FetchyEvent<>(serviceId, name, node, elapsedMillis, target);
			for (Object entry : listeners.entrySet()) {
				try {
					Consumer consumer = (Consumer) ((Map.Entry) entry).getValue();
					consumer.accept(event);
				} catch (RuntimeException ex) {
					LOG.error("Error calling listener " + ((Map.Entry) entry).getKey() + "... ignoring", ex);
				}
			}
		});
	}

	@Override
	public void start() {
		LOG.info("Booting up Fetchy");
	}

	@Override
	public void stop() {
		LOG.info("Shutting down Fetchy");
		if (executorService != null) {
			executorService.shutdown();
		}
		discos.clear();
		bals.clear();
		cons.clear();
		discoverListeners.clear();
		balanceListeners.clear();
		connectListeners.clear();
		requestListeners.clear();
		errorListeners.clear();
		LOG.info("Fetchy shutdown complete");
	}

	@Override
	public <ERROR extends Exception> boolean isRunning() throws ERROR {
		return true;
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

	public List<URI> discover(String serviceId) {
		LOG.debug("Discovering service {}", serviceId);
		Discoverer discoverer = getServiceDiscoverer(serviceId);
		if (discoverer == null) {
			throw new NoDiscovererException("No discoverer registered for service " + serviceId);
		}
		return discoverer.discover(serviceId);
	}

	public URI balance(String serviceId, List<URI> nodes) {
		LOG.debug("Balancing service {} across {} nodes", serviceId, nodes == null ? 0 : nodes.size());
		Balancer balancer = getServiceBalancer(serviceId);
		if (balancer == null) {
			throw new NoBalancerException("No balancer registered for service " + serviceId);
		}
		return balancer.balance(nodes);
	}

	public <API> API connect(String serviceId, URI node) {
		LOG.debug("Connecting service {} to node at {}", serviceId, node);
		Connector<API> connector = getServiceConnector(serviceId);
		if (connector == null) {
			throw new NoConnectorException("No connector registered for service " + serviceId);
		}
		return connector.connect(node);
	}

	public String onDiscover(Consumer<FetchyEvent<List<URI>>> listener) {
		return on("discover", discoverListenerId, discoverListeners, listener);
	}

	public String onBalance(Consumer<FetchyEvent<URI>> listener) {
		return on("balance", balanceListenerId, balanceListeners, listener);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <API> String onConnect(Consumer<FetchyEvent<API>> listener) {
		return on("connect", connectListenerId, connectListeners, (Consumer) listener);
	}

	public String onRequest(Consumer<FetchyEvent<?>> listener) {
		return on("request", requestListenerId, requestListeners, listener);
	}

	public String onError(Consumer<FetchyEvent<Throwable>> listener) {
		return on("error", errorListenerId, errorListeners, listener);
	}

	private <OBJECT> String on(String entity, AtomicInteger idGenerator, Map<String, Consumer<OBJECT>> listeners,
			Consumer<OBJECT> listener) {
		LOG.debug("Registering {} listener {}", entity, listener);
		if (listener == null) {
			throw new IllegalArgumentException("Listener cannot be null");
		}
		String id = entity + "-listener:" + idGenerator.incrementAndGet();
		listeners.put(id, listener);
		return id;
	}

	public void removeListener(String listenerId) {
		if (listenerId == null) {
			throw new IllegalArgumentException("Listener ID cannot be null");
		}
		String entity = listenerId.substring(0, listenerId.indexOf('-'));
		switch (entity) {
		case EVENT_DISCOVER:
			discoverListeners.remove(listenerId);
			break;
		case EVENT_BALANCE:
			balanceListeners.remove(listenerId);
			break;
		case EVENT_CONNECT:
			connectListeners.remove(listenerId);
			break;
		case EVENT_REQUEST:
			requestListeners.remove(listenerId);
			break;
		case EVENT_ERROR:
			errorListeners.remove(listenerId);
			break;
		}
	}

	public <API> RequestBuilder<API> createRequest(String serviceId, Class<API> apiClass) {
		return createRequest(serviceId, (String) null);
	}

	public <API> RequestBuilder<API> createRequest(String serviceId, String requestName, Class<API> apiClass) {
		return createRequest(serviceId, requestName);
	}

	public <API> RequestBuilder<API> createRequest(String serviceId) {
		return createRequest(serviceId, (String) null);
	}

	public <API> RequestBuilder<API> createRequest(String serviceId, String requestName) {
		RequestBuilder<API> result = new RequestBuilder<>(this);
		result.service(serviceId);
		return result.name(requestName == null ? "request@" + serviceId : requestName);
	}

	public <OUTPUT, API, ERROR extends Exception> OUTPUT call(String serviceId, Class<API> apiClass,
			Call<OUTPUT, API, ERROR> call) throws ERROR {
		return call(serviceId, (String) null, call);
	}

	public <OUTPUT, API, ERROR extends Exception> OUTPUT call(String serviceId, String requestName, Class<API> apiClass,
			Call<OUTPUT, API, ERROR> call) throws ERROR {
		return call(serviceId, requestName, call);
	}

	public <OUTPUT, API, ERROR extends Exception> OUTPUT call(String serviceId, String requestName,
			Call<OUTPUT, API, ERROR> call) throws ERROR {
		RequestBuilder<API> rb = createRequest(serviceId, requestName);
		return rb.callable(call).build().execute();
	}

	public <API, ERROR extends Exception> void run(String serviceId, Class<API> apiClass, Run<API, ERROR> run)
			throws ERROR {
		run(serviceId, (String) null, run);
	}

	public <API, ERROR extends Exception> void run(String serviceId, String requestName, Class<API> apiClass,
			Run<API, ERROR> run) throws ERROR {
		run(serviceId, requestName, run);
	}

	public <API, ERROR extends Exception> void run(String serviceId, String requestName, Run<API, ERROR> run)
			throws ERROR {
		RequestBuilder<API> rb = createRequest(serviceId, requestName);
		rb.runnable(run).build().execute();
	}

}

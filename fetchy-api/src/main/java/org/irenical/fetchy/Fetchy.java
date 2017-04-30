package org.irenical.fetchy;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Fetchy {

	private final Map<String, Discoverer> discos = new ConcurrentHashMap<>();

	private final Map<String, Balancer> bals = new ConcurrentHashMap<>();

	private final Map<String, Connector<?>> cons = new ConcurrentHashMap<>();

	public void registerDiscoverer(String serviceId, Discoverer discoverer) {
		if (serviceId == null) {
			throw new IllegalArgumentException("Service ID cannot be null");
		}
		discos.put(serviceId, discoverer);
	}

	public void registerBalancer(String serviceId, Balancer balancer) {
		if (serviceId == null) {
			throw new IllegalArgumentException("Service ID cannot be null");
		}
		bals.put(serviceId, balancer);
	}

	public void registerConnector(String serviceId, Connector<?> connector) {
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
		Discoverer discoverer = getServiceDiscoverer(serviceId);
		if (discoverer == null) {
			throw new NoDiscovererException("No discoverer registered for service " + serviceId);
		}
		return discoverer.discover(serviceId);
	}

	public URI balance(String serviceId, List<URI> nodes) {
		Balancer balancer = getServiceBalancer(serviceId);
		if (balancer == null) {
			throw new NoBalancerException("No balancer registered for service " + serviceId);
		}
		return balancer.balance(nodes);
	}

	public <API> API connect(String serviceId, URI node) {
		Connector<API> connector = getServiceConnector(serviceId);
		if (connector == null) {
			throw new NoConnectorException("No connector registered for service " + serviceId);
		}
		return connector.connect(node);
	}
	
	public <API> RequestBuilder<API> createRequest(String serviceId, Class<API> apiClass) {
		return createRequest(serviceId);
	}

	public <API> RequestBuilder<API> createRequest(String serviceId) {
		RequestBuilder<API> result = new RequestBuilder<>(this);
		return result.service(serviceId);
	}

	public <OUTPUT, API, ERROR extends Exception> OUTPUT call(String serviceId, Class<API> apiClass,
			Call<OUTPUT, API, ERROR> call) throws ERROR {
		return call(serviceId, call);
	}

	public <OUTPUT, API, ERROR extends Exception> OUTPUT call(String serviceId, Call<OUTPUT, API, ERROR> call)
			throws ERROR {
		RequestBuilder<API> rb = createRequest(serviceId);
		return rb.callable(call).build().execute();
	}

	public <API, ERROR extends Exception> void run(String serviceId, Class<API> apiClass, Run<API, ERROR> run)
			throws ERROR {
		run(serviceId, run);
	}

	public <API, ERROR extends Exception> void run(String serviceId, Run<API, ERROR> run) throws ERROR {
		RequestBuilder<API> rb = createRequest(serviceId);
		rb.runnable(run).build().execute();
	}

}
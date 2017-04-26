package org.irenical.fetchy;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Fetchy {

	private final Map<String, Discoverer> discos = new ConcurrentHashMap<>();

	private final Map<String, Balancer> bals = new ConcurrentHashMap<>();

	private final Map<String, Connector<?>> cons = new ConcurrentHashMap<>();

	public void register(String serviceId, Discoverer discoverer) {
		if (serviceId == null) {
			throw new IllegalArgumentException("Service ID cannot be null");
		}
		discos.put(serviceId, discoverer);
	}

	public void register(String serviceId, Balancer balancer) {
		if (serviceId == null) {
			throw new IllegalArgumentException("Service ID cannot be null");
		}
		bals.put(serviceId, balancer);
	}

	public void register(String serviceId, Connector<?> connector) {
		if (serviceId == null) {
			throw new IllegalArgumentException("Service ID cannot be null");
		}
		if (connector == null) {
			throw new IllegalArgumentException("Service Connector cannot be null");
		}
		cons.put(serviceId, connector);
	}

	public void register(String serviceId, Discoverer discoverer, Balancer balancer, Connector<?> connector) {
		register(serviceId, discoverer);
		register(serviceId, balancer);
		register(serviceId, connector);
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
	
	public <API, OUTPUT, ERROR extends Exception> OUTPUT callReturning(String serviceId, Call<OUTPUT, API, ERROR> lambda) {
		CallBuilder<API> callBuilder = createCall(serviceId);
		callBuilder.returning(lambda);
		return callBuilder.call();
	}
	
	public <API, OUTPUT, ERROR extends Exception> OUTPUT callNonReturning(String serviceId, Run<OUTPUT, API, ERROR> lambda) {
		CallBuilder<API> callBuilder = createCall(serviceId);
		callBuilder.nonreturning(lambda);
		return callBuilder.call();
	}

	public <API> CallBuilder<API> createCall(String serviceId) {
		List<URI> discovery = discover(serviceId);
		URI node = balance(serviceId, discovery);
		return createCall(serviceId, node);
	}

	private <API> CallBuilder<API> createCall(String serviceId, URI node) {
		CallBuilder<API> result = new CallBuilder<>();
		result.service(serviceId);
		result.node(node);
		Connector<API> con = getServiceConnector(serviceId);
		API api = con.getStub(node);
		result.stub(api);
		return result;
	}

	private URI balance(String serviceId, List<URI> discovery) {
		URI result = null;
		if (discovery != null && discovery.size() > 1) {
			Balancer bal = getServiceBalancer(serviceId);
			if (bal != null) {
				result = bal.balance(discovery);
			} else {
				return discovery.get(0);
			}
		}
		return result;
	}

	private List<URI> discover(String serviceId) {
		Discoverer disco = getServiceDiscoverer(serviceId);
		return disco == null ? null : disco.discover(serviceId);
	}

}

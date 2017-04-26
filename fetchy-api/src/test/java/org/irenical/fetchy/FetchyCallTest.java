package org.irenical.fetchy;

import org.junit.Assert;
import org.junit.Test;

public class FetchyCallTest {

	private String serviceId = "myTestService";

	private Fetchy fetchy = new Fetchy();

	// Connector
	@Test
	public void testConnectorRegistration() {
		Connector<?> con = uri -> null;
		fetchy.register(serviceId, con);
		Assert.assertEquals(con, fetchy.getServiceConnector(serviceId));
	}

	@Test
	public void testNoConnector() {
		Assert.assertEquals(null, fetchy.getServiceConnector(serviceId));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConnectorRegistration() {
		fetchy.register(serviceId, (Connector<?>) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConnectorIdRegistration() {
		fetchy.register(null, (Connector<?>) uri -> null);
	}

	// Discoverer
	@Test
	public void testDiscovererRegistration() {
		Discoverer disco = id -> null;
		fetchy.register(serviceId, disco);
		Assert.assertEquals(disco, fetchy.getServiceDiscoverer(serviceId));
	}

	@Test
	public void testNoDiscoverer() {
		Assert.assertEquals(null, fetchy.getServiceDiscoverer(serviceId));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidDiscovererRegistration() {
		fetchy.register(null, (Discoverer) uri -> null);
	}

	// Balancer
	@Test
	public void testBalancerRegistration() {
		Balancer bal = urls -> null;
		fetchy.register(serviceId, bal);
		Assert.assertEquals(bal, fetchy.getServiceBalancer(serviceId));
	}

	@Test
	public void testNoBalancer() {
		Assert.assertEquals(null, fetchy.getServiceBalancer(serviceId));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidBalancerRegistration() {
		fetchy.register(null, (Balancer) uri -> null);
	}

	// Service
	@Test
	public void testFullRegistration() {
		Connector<?> con = uri -> null;
		Discoverer disco = id -> null;
		Balancer bal = urls -> null;
		fetchy.register(serviceId, disco, bal, con);
		Assert.assertEquals(con, fetchy.getServiceConnector(serviceId));
		Assert.assertEquals(disco, fetchy.getServiceDiscoverer(serviceId));
		Assert.assertEquals(bal, fetchy.getServiceBalancer(serviceId));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidIdFullRegistration() {
		Connector<?> con = uri -> null;
		Discoverer disco = id -> null;
		Balancer bal = urls -> null;
		fetchy.register(null, disco, bal, con);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidConnectorFullRegistration() {
		Connector<?> con = null;
		Discoverer disco = id -> null;
		Balancer bal = urls -> null;
		fetchy.register(null, disco, bal, con);
	}

}

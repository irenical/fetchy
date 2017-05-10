package org.irenical.fetchy.discoverer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.irenical.fetchy.Fetchy;
import org.irenical.fetchy.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DiscoverTest {

	private String nonExistingServiceId = "nonexisting";

	private String faultyServiceId = "faultyService";

	private Discoverer faultyDisco = id -> {throw new DiscoverException("blew up");};

	private String nullServiceId = "nullService";

	private Discoverer nullDisco = id -> null;

	private String emptyServiceId = "emptyService";

	private Discoverer emptyDisco = id -> Collections.emptyList();

	private String singleServiceId = "singleService";

	private static final Node SINGLE_NODE = new Node("http://localhost:80");

	private Discoverer singleDisco = id -> Collections.singletonList(SINGLE_NODE);
	
	private String multipleServiceId = "multipleService";

	private static final Node MULTI_NODE_FIRST = new Node("http://127.0.0.1:81");
	private static final Node MULTI_NODE_SECOND = new Node("http://[::1]:82");

	private Discoverer multipleDisco = id -> Arrays.asList(MULTI_NODE_FIRST, MULTI_NODE_SECOND);
	
	private Fetchy fetchy = new Fetchy();
	
	@Before
	public void prepare() {
		fetchy.registerDiscoverer(nullServiceId, nullDisco);
		fetchy.registerDiscoverer(emptyServiceId, emptyDisco);
		fetchy.registerDiscoverer(singleServiceId, singleDisco);
		fetchy.registerDiscoverer(multipleServiceId, multipleDisco);
		fetchy.registerDiscoverer(faultyServiceId, faultyDisco);
	}

	// Discoverer
	@Test(expected=DiscovererMissingException.class)
	public void testNoDiscoverer() throws DiscoverException, DiscovererMissingException {
		fetchy.discover(nonExistingServiceId);
	}
	
	@Test(expected=DiscoverException.class)
	public void testFaultyDiscoverer() throws DiscoverException, DiscovererMissingException {
		fetchy.discover(faultyServiceId);
	}
	
	@Test
	public void testNullDiscovery() throws DiscoverException, DiscovererMissingException {
		List<Node> got = fetchy.discover(nullServiceId);
		Assert.assertNull(got);
	}
	
	@Test
	public void testEmptyDiscovery() throws DiscoverException, DiscovererMissingException {
		List<Node> got = fetchy.discover(emptyServiceId);
		Assert.assertNotNull(got);
		Assert.assertEquals(0,got.size());
	}
	
	@Test
	public void testSingleDiscovery() throws DiscoverException, DiscovererMissingException {
		List<Node> got = fetchy.discover(singleServiceId);
		Assert.assertNotNull(got);
		Assert.assertEquals(1,got.size());
		Node node = got.get(0);
		Assert.assertNotNull(node);
		Assert.assertEquals(SINGLE_NODE, node);
	}

	@Test
	public void testMultipleDiscovery() throws DiscoverException, DiscovererMissingException {
		List<Node> got = fetchy.discover(multipleServiceId);
		Assert.assertNotNull(got);
		Assert.assertEquals(2,got.size());
		Node node1 = got.get(0);
		Assert.assertNotNull(node1);
		Assert.assertEquals(MULTI_NODE_FIRST, node1);

		Node node2 = got.get(1);
		Assert.assertNotNull(node2);
		Assert.assertEquals(MULTI_NODE_SECOND, node2);
	}

}

package org.irenical.fetchy;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	
	private Discoverer singleDisco = id -> Collections.singletonList(URI.create("http://localhost:80"));
	
	private String multipleServiceId = "multipleService";
	
	private Discoverer multipleDisco = id -> Arrays.asList(URI.create("http://127.0.0.1:81"), URI.create("http://[::1]:82"));
	
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
	@Test(expected=NoDiscovererException.class)
	public void testNoDiscoverer() throws DiscoverException, NoDiscovererException {
		fetchy.discover(nonExistingServiceId);
	}
	
	@Test(expected=DiscoverException.class)
	public void testFaultyDiscoverer() throws DiscoverException, NoDiscovererException {
		fetchy.discover(faultyServiceId);
	}
	
	@Test
	public void testNullDiscovery() throws DiscoverException, NoDiscovererException {
		List<URI> got = fetchy.discover(nullServiceId);
		Assert.assertNull(got);
	}
	
	@Test
	public void testEmptyDiscovery() throws DiscoverException, NoDiscovererException {
		List<URI> got = fetchy.discover(emptyServiceId);
		Assert.assertNotNull(got);
		Assert.assertEquals(0,got.size());
	}
	
	@Test
	public void testSingleDiscovery() throws DiscoverException, NoDiscovererException {
		List<URI> got = fetchy.discover(singleServiceId);
		Assert.assertNotNull(got);
		Assert.assertEquals(1,got.size());
		URI uri = got.get(0);
		Assert.assertNotNull(uri);
		Assert.assertEquals("http", uri.getScheme());
		Assert.assertEquals("localhost", uri.getHost());
		Assert.assertEquals(80, uri.getPort());
	}
	
	@Test
	public void testMultipleDiscovery() throws DiscoverException, NoDiscovererException {
		List<URI> got = fetchy.discover(multipleServiceId);
		Assert.assertNotNull(got);
		Assert.assertEquals(2,got.size());
		URI uri1 = got.get(0);
		Assert.assertNotNull(uri1);
		Assert.assertEquals("http", uri1.getScheme());
		Assert.assertEquals("127.0.0.1", uri1.getHost());
		Assert.assertEquals(81, uri1.getPort());
		
		URI uri2 = got.get(1);
		Assert.assertNotNull(uri2);
		Assert.assertEquals("http", uri2.getScheme());
		Assert.assertEquals("[::1]", uri2.getHost());
		Assert.assertEquals(82, uri2.getPort());
	}

}

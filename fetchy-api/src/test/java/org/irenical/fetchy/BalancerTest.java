package org.irenical.fetchy;

import java.net.URI;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BalancerTest {

	private String nonExistingServiceId = "nonexisting";
	
	private String faultyServiceId = "faultyService";
	
	private Balancer faultyBalancer = uris -> {throw new BalanceException("blew up");};
	
	private String nullServiceId = "nullService";
	
	private Balancer nullBalancer = uris -> null;

	private String firstServiceId = "singleService";
	
	private Balancer firstBalancer = uris -> uris.get(0);
	
	private Fetchy fetchy = new Fetchy();
	
	@Before
	public void prepare(){
		fetchy.registerBalancer(faultyServiceId, faultyBalancer);
		fetchy.registerBalancer(nullServiceId, nullBalancer);
		fetchy.registerBalancer(firstServiceId, firstBalancer);
	}
	
	// Balancer
	@Test(expected=NoBalancerException.class)
	public void testNoBalancer() throws NoBalancerException, BalanceException {
		fetchy.balance(nonExistingServiceId, null);
	}
	
	@Test(expected=BalanceException.class)
	public void testFaultyBalancer() throws NoBalancerException, BalanceException {
		fetchy.balance(faultyServiceId, null);
	}
	
	@Test
	public void testNullBalancer() throws NoBalancerException, BalanceException {
		URI uri1 = URI.create("http://127.0.0.1:81");
		URI uri2 = URI.create("http://[::1]:82");
		URI got = fetchy.balance(nullServiceId, Arrays.asList(uri1, uri2));
		Assert.assertNull(got);
	}
	
	@Test
	public void testFirstBalancer() throws NoBalancerException, BalanceException {
		URI uri1 = URI.create("http://127.0.0.1:81");
		URI uri2 = URI.create("http://[::1]:82");
		URI got = fetchy.balance(firstServiceId, Arrays.asList(uri1, uri2));
		Assert.assertNotNull(got);
		Assert.assertEquals(uri1, got);
	}


}

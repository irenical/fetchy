package org.irenical.fetchy.balancer;

import java.util.Arrays;

import org.irenical.fetchy.Fetchy;
import org.irenical.fetchy.Node;
import org.irenical.fetchy.balancer.BalanceException;
import org.irenical.fetchy.balancer.Balancer;
import org.irenical.fetchy.balancer.BalancerMissingException;
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
	@Test(expected=BalancerMissingException.class)
	public void testNoBalancer() throws BalancerMissingException, BalanceException {
		fetchy.balance(nonExistingServiceId, null);
	}
	
	@Test(expected=BalanceException.class)
	public void testFaultyBalancer() throws BalancerMissingException, BalanceException {
		fetchy.balance(faultyServiceId, null);
	}
	
	@Test
	public void testNullBalancer() throws BalancerMissingException, BalanceException {
		Node uri1 = new Node("http://127.0.0.1:81");
		Node uri2 = new Node("http://[::1]:82");
		Node got = fetchy.balance(nullServiceId, Arrays.asList(uri1, uri2));
		Assert.assertNull(got);
	}
	
	@Test
	public void testFirstBalancer() throws BalancerMissingException, BalanceException {
		Node uri1 = new Node("http://127.0.0.1:81");
		Node uri2 = new Node("http://[::1]:82");
		Node got = fetchy.balance(firstServiceId, Arrays.asList(uri1, uri2));
		Assert.assertNotNull(got);
		Assert.assertEquals(uri1, got);
	}


}

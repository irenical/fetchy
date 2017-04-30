package org.irenical.fetchy;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.irenical.fetchy.mock.MockService;
import org.junit.Assert;
import org.junit.Test;

public class SimpleCallTest {

	private String serviceId = "serviceId";

	private Fetchy fetchy = new Fetchy();

	@Test
	public void testConnectorOnlyCall() {
		String output = "Hello";
		
		fetchy.registerConnector(serviceId, uri -> new MockService(output));
		
		String outcome = fetchy.call(serviceId, MockService.class, api -> api.getSomething());
		Assert.assertEquals(outcome, output);
	}
	
	@Test
	public void testSingleNodeNoBalancerCall() {
		String output = "Hello";
		URI correctUri = URI.create("http://localhost:1337/api");

		fetchy.registerDiscoverer(serviceId, sid -> Collections.singletonList(correctUri));
		fetchy.registerConnector(serviceId, uri -> new MockService(output));

		String outcome = fetchy.call(serviceId, MockService.class, api -> api.getSomething());
		Assert.assertEquals(outcome, output);
	}
	
	@Test
	public void testDualNodeDefaultBalancerRun() {
		MockService service1 = new MockService(null);
		MockService service2 = new MockService(null);
		URI uri1 = URI.create("http://localhost:1337/api");
		URI uri2 = URI.create("http://localhost:1336/api");

		fetchy.registerDiscoverer(serviceId, sid -> Arrays.asList(uri1,uri2));
		fetchy.registerConnector(serviceId, uri -> {
			if(uri1.equals(uri)){
				return service1;
			} else {
				return service2;
			}
		});

		fetchy.run(serviceId, MockService.class, api -> api.doSomething());
		
		Assert.assertTrue(service1.ran);
		Assert.assertFalse(service2.ran);
	}
	
	@Test
	public void testDualNodeCustomBalancerRun() {
		MockService service1 = new MockService(null);
		MockService service2 = new MockService(null);
		
		URI uri1 = URI.create("http://localhost:1337/api");
		URI uri2 = URI.create("http://localhost:1336/api");

		fetchy.registerDiscoverer(serviceId, sid -> Arrays.asList(uri1,uri2));
		fetchy.registerBalancer(serviceId, uris -> uris.get(1));
		fetchy.registerConnector(serviceId, uri -> {
			if(uri1.equals(uri)){
				return service1;
			} else {
				return service2;
			}
		});
		fetchy.run(serviceId, MockService.class, api -> api.doSomething());
		Assert.assertFalse(service1.ran);
		Assert.assertTrue(service2.ran);
	}
	
	@Test
	public void testDualNodeCustomBalancerCall() {
		String output1 = "Hello1";
		String output2 = "Hello2";
		
		MockService service1 = new MockService(output1);
		MockService service2 = new MockService(output2);
		
		URI uri1 = URI.create("http://localhost:1337/api");
		URI uri2 = URI.create("http://localhost:1336/api");

		fetchy.registerDiscoverer(serviceId, sid -> Arrays.asList(uri1,uri2));
		fetchy.registerBalancer(serviceId, uris -> uris.get(1));
		fetchy.registerConnector(serviceId, uri -> {
			if(uri1.equals(uri)){
				return service1;
			} else {
				return service2;
			}
		});
		String got = fetchy.call(serviceId, MockService.class, api -> api.getSomething());
		Assert.assertEquals(output2, got);
	}
	
	
	@Test
	public void testConnectorOnlyRun() {
		MockService service = new MockService(null);
		fetchy.registerConnector(serviceId, uri -> service);
		
		fetchy.run(serviceId, MockService.class, api -> api.doSomething());
		Assert.assertTrue(service.ran);
	}
	
	@Test
	public void testSingleNodeNoBalancerRun() {
		MockService service = new MockService(null);
		URI correctUri = URI.create("http://localhost:1337/api");

		fetchy.registerDiscoverer(serviceId, sid -> Collections.singletonList(correctUri));
		fetchy.registerConnector(serviceId, uri -> service);

		fetchy.run(serviceId, MockService.class, api -> api.doSomething());
		Assert.assertTrue(service.ran);
	}

}

package org.irenical.fetchy;

import java.net.URI;
import java.util.Collections;

import org.irenical.fetchy.mock.MockService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ListenerTest {
	
	private long timeout = 1000;

	private String serviceId = "serviceId";

	private String callname = "MyCall";

	private Fetchy fetchy = new Fetchy();

	private String output = "Hello";

	private URI node = URI.create("http://localhost:1337/api");

	private FetchyEvent<?> got = null;

	@Before
	public void prepare() {
		fetchy.registerDiscoverer(serviceId, sid -> Collections.singletonList(node));
		fetchy.registerConnector(serviceId, uri -> new MockService(output));
	}

	@Test
	public synchronized void testOnRequestCallListener() throws InterruptedException {
		String lid = fetchy.onRequest(e -> {
			Assert.assertEquals(serviceId, e.getServiceId());
			Assert.assertEquals(node, e.getNode());
			Assert.assertEquals(callname, e.getName());
			Assert.assertTrue(e.getElapsedMillis() >= 0);
			Assert.assertTrue(e.getElapsedMillis() < timeout);
			got = e;
			synchronized (ListenerTest.this) {
				ListenerTest.this.notify();
			}
		});
		
		fetchy.call(serviceId, callname, MockService.class, api -> api.getSomething());
		this.wait(timeout);
		Assert.assertNotNull(got);
		
		got = null;
		fetchy.removeListener(lid);
		
		fetchy.call(serviceId, callname, MockService.class, api -> api.getSomething());
		this.wait(timeout);
		Assert.assertNull(got);
		
	}

}

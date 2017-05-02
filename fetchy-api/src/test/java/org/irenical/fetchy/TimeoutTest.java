package org.irenical.fetchy;

import java.util.concurrent.TimeUnit;

import org.irenical.fetchy.mock.MockService;
import org.irenical.fetchy.mock.SomethingWrongException;
import org.junit.Assert;
import org.junit.Test;

public class TimeoutTest {
	
	private long methodRunTime = 10000;
	
	private int timeout = 100;
	
	private int maximumAcceptableTimeout = timeout * 2;

	private String output = "Hello";

	private String fallbackOutput = "Bye";

	private String serviceId = "serviceId";

	private Fetchy fetchy = new Fetchy();

	@Test
	public void testFallbackOnServiceCall() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> null);
		fetchy.registerConnector(serviceId, uri -> new MockService(output));
		long before = System.nanoTime();
		String got = fetchy.createRequest(serviceId, MockService.class).callable(api -> api.getSomethingSlowly(methodRunTime))
				.fallback(e -> fallbackOutput).timeout(timeout).build().execute();
		long delta = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - before);
		System.out.println(delta);
		Assert.assertTrue(delta < maximumAcceptableTimeout);
		Assert.assertEquals(fallbackOutput, got);
	}

}

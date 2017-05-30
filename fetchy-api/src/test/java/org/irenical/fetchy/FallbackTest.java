package org.irenical.fetchy;

import org.irenical.fetchy.balancer.BalanceException;
import org.irenical.fetchy.connector.ConnectException;
import org.irenical.fetchy.discoverer.DiscoverException;
import org.irenical.fetchy.utils.mock.MockService;
import org.irenical.fetchy.utils.mock.SomethingWrongException;
import org.junit.Assert;
import org.junit.Test;

public class FallbackTest {

	private String output = "Hello";

	private String fallbackOutput = "Bye";

	private boolean fallbackRan = false;

	private String serviceId = "serviceId";

	private Fetchy fetchy = new Fetchy();

	@Test(expected = DiscoverException.class)
	public void testNoFallbackOnDiscoverCall() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> {
			throw new DiscoverException("Blew up");
		});
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));

		fetchy.call(serviceId, MockService.class, MockService::getSomethingWrong);
	}

	@Test(expected = DiscoverException.class)
	public void testNoFallbackOnDiscoverRun() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> {
			throw new DiscoverException("Blew up");
		});
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));

		fetchy.run(serviceId, MockService.class, MockService::doSomethingWrong);
	}

	@Test
	public void testFallbackOnDiscoverCall() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> {
			throw new DiscoverException("Blew up");
		});
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));

		String got = fetchy.createRequest(serviceId, MockService.class).callable(MockService::getSomethingWrong)
				.fallback(e -> fallbackOutput).build().execute();

		Assert.assertEquals(fallbackOutput, got);
	}

	@Test
	public void testFallbackOnDiscoverRun() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> {
			throw new DiscoverException("Blew up");
		});
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));

		fetchy.createRequest(serviceId, MockService.class).runnable(MockService::doSomethingWrong).fallback(e -> {
			fallbackRan = true;
		}).build().execute();

		Assert.assertTrue(fallbackRan);
	}

	@Test(expected = BalanceException.class)
	public void testNoFallbackOnBalancingCall() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> {
			throw new BalanceException("Blew up");
		});
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));

		fetchy.call(serviceId, MockService.class, MockService::getSomethingWrong);
	}

	@Test
	public void testFallbackOnBalancingCall() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> {
			throw new BalanceException("Blew up");
		});
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));

		String got = fetchy.createRequest(serviceId, MockService.class).callable(MockService::getSomethingWrong)
				.fallback(e -> fallbackOutput).build().execute();

		Assert.assertEquals(fallbackOutput, got);
	}

	@Test(expected = ConnectException.class)
	public void testNoFallbackOnConnectCall() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> null);
		fetchy.registerConnector(serviceId, uri -> {
			throw new ConnectException("Blew up");
		});
		fetchy.call(serviceId, MockService.class, MockService::getSomethingWrong);
	}

	@Test
	public void testFallbackOnConnectCall() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> null);
		fetchy.registerConnector(serviceId, uri -> {
			throw new ConnectException("Blew up");
		});
		String got = fetchy.createRequest(serviceId, MockService.class).callable(MockService::getSomethingWrong)
				.fallback(e -> fallbackOutput).build().execute();
		Assert.assertEquals(fallbackOutput, got);
	}

	@Test(expected = SomethingWrongException.class)
	public void testNoFallbackOnServiceCall() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> null);
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));
		fetchy.call(serviceId, MockService.class, MockService::getSomethingWrong);
	}

	@Test(expected = SomethingWrongException.class)
	public void testNoFallbackOnServiceRun() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> null);
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));
		fetchy.run(serviceId, MockService.class, MockService::doSomethingWrong);
	}

	@Test
	public void testFallbackOnServiceCall() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> null);
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));
		String got = fetchy.createRequest(serviceId, MockService.class).callable(MockService::getSomethingWrong)
				.fallback(e -> fallbackOutput).build().execute();
		Assert.assertEquals(fallbackOutput, got);
	}

	@Test
	public void testFallbackOnServiceRun() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> null);
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));
		fetchy.createRequest(serviceId, MockService.class).runnable(MockService::doSomethingWrong).fallback(e -> {
			fallbackRan = true;
		}).build().execute();
		Assert.assertTrue(fallbackRan);
	}

	@Test(expected = SomethingWrongException.class)
	public void testFallbackErrorOnServiceRun() throws SomethingWrongException {
		fetchy.registerDiscoverer(serviceId, sid -> null);
		fetchy.registerBalancer(serviceId, uris -> null);
		fetchy.registerConnector(serviceId, uri -> () -> new MockService(output));
		fetchy.createRequest(serviceId, MockService.class).runnable(MockService::doSomethingWrong).fallback(e -> {
			throw new RuntimeException();
		}).build().execute();
	}

}

package org.irenical.fetchy;

import java.net.URI;
import java.util.Collections;

import org.irenical.fetchy.mock.MockService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ListenerTest {

	private String serviceId = "serviceId";

	private Fetchy fetchy = new Fetchy();

	private String output = "Hello";

	private URI node = URI.create("http://localhost:1337/api");
	
	private RequestResolvedEvent got = null;

	@Before
	public void prepare() {
		fetchy.registerDiscoverer(serviceId, sid -> Collections.singletonList(node));
		fetchy.registerConnector(serviceId, uri -> new MockService(output));
	}

	@Test
	public void testCallListener() throws InterruptedException {
		fetchy.listen(e->{got=e;});
		String outcome = fetchy.call(serviceId, MockService.class, api -> api.getSomething());
		Assert.assertEquals(outcome, output);
		int count = 10;
		while(got==null && count > 0) {
			Thread.sleep(100);
			--count;
		}
		Assert.assertNotNull(got);
	}

}

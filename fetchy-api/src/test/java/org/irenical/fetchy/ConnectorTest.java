package org.irenical.fetchy;

import org.irenical.fetchy.mock.MockService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConnectorTest {

	private String nonExistingServiceId = "nonexisting";

	private String faultyServiceId = "faultyService";

	private Connector<MockService> faultyConnector = uri -> {
		throw new ConnectException("blew up");
	};

	private String nullServiceId = "nullService";

	private Connector<MockService> nullConnector = uri -> null;

	private String workingServiceId = "workingService";

	private MockService service = new MockService("irrelevant");

	private Connector<MockService> workingConnector = uri -> service;

	private Fetchy fetchy = new Fetchy();

	@Before
	public void prepare() {
		fetchy.registerConnector(faultyServiceId, faultyConnector);
		fetchy.registerConnector(nullServiceId, nullConnector);
		fetchy.registerConnector(workingServiceId, workingConnector);
	}

	// Balancer
	@Test(expected = NoConnectorException.class)
	public void testNoConnector() throws NoConnectorException, ConnectException {
		fetchy.connect(nonExistingServiceId, null);
	}

	@Test(expected = ConnectException.class)
	public void testFaultyConnector() throws NoConnectorException, ConnectException {
		fetchy.connect(faultyServiceId, null);
	}

	@Test
	public void testNullConnector() throws NoConnectorException, ConnectException {
		MockService got = fetchy.connect(nullServiceId, null);
		Assert.assertNull(got);
	}

	@Test
	public void testWorkingConnector() throws NoConnectorException, ConnectException {
		MockService got = fetchy.connect(workingServiceId, null);
		Assert.assertNotNull(got);
		Assert.assertEquals(service, got);
	}

}
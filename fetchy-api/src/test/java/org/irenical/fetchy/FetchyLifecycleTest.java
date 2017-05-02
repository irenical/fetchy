package org.irenical.fetchy;

import java.util.concurrent.ExecutorService;

import org.junit.Assert;
import org.junit.Test;

public class FetchyLifecycleTest {
	
	private Fetchy fetchy = new Fetchy();

	@Test
	public void testLifeCycle() {
		fetchy.start();
		Assert.assertTrue(fetchy.isRunning());
		ExecutorService es =fetchy.getExecutorService();
		fetchy.stop();
		Assert.assertTrue(es.isShutdown());
	}
	

}

package org.irenical.fetchy.engine;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

public class FetchyEngineLifecycleTest {

    private FetchyEngine fetchyEngine = new FetchyEngine();

    @Test
    public void testLifeCycle() {
        fetchyEngine.start();
        Assert.assertTrue(fetchyEngine.isRunning());
        ExecutorService es = fetchyEngine.getExecutorService();
        fetchyEngine.stop();
        Assert.assertTrue(es.isShutdown());
    }
}

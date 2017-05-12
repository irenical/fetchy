package org.irenical.fetchy.event;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ExecutorService;

@RunWith(MockitoJUnitRunner.class)
public class EventEmitterLifecycleTest {
    @Mock
    private ExecutorService executorService;

    @Test
    public void stop() throws Exception {
        final EventEmitter emitter = new EventEmitter(executorService);

        emitter.start();
        Assert.assertTrue(emitter.isRunning());
        emitter.stop();

        Mockito.verify(executorService).shutdown();
    }

}

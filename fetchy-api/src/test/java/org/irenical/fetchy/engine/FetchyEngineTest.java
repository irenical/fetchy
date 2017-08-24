package org.irenical.fetchy.engine;

import org.irenical.fetchy.event.EventEmitter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FetchyEngineTest {
    @Mock
    private EventEmitter emitter;

    private FetchyEngine engine;

    @Before
    public void setUp() throws Exception {
        engine = new FetchyEngine(emitter);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testListenersRegistered() throws Exception {
        final Consumer discoverConsumer = mock(Consumer.class);

        engine.onDiscover(discoverConsumer);
        verify(emitter).addListener(eq(FetchyEngine.EVENT_DISCOVER), eq(discoverConsumer));

        final Consumer balanceConsumer = mock(Consumer.class);

        engine.onBalance(balanceConsumer);
        verify(emitter).addListener(eq(FetchyEngine.EVENT_BALANCE), eq(balanceConsumer));

        final Consumer connectConsumer = mock(Consumer.class);

        engine.onConnect(connectConsumer);
        verify(emitter).addListener(eq(FetchyEngine.EVENT_CONNECT), eq(connectConsumer));

        final Consumer requestConsumer = mock(Consumer.class);

        engine.onRequest(requestConsumer);
        verify(emitter).addListener(eq(FetchyEngine.EVENT_REQUEST), eq(requestConsumer));

        final Consumer errorConsumer = mock(Consumer.class);

        engine.onError(errorConsumer);
        verify(emitter).addListener(eq(FetchyEngine.EVENT_ERROR), eq(errorConsumer));
    }

    @Test
    public void testListenersRemoved() throws Exception {
        String A_MOCK_LISTENER_ID = "a-mock-listener-id";

        engine.removeListener(A_MOCK_LISTENER_ID);

        verify(emitter).removeListener(A_MOCK_LISTENER_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveListenerFailsOnMissingId() throws Exception {
        engine.removeListener(null);
    }
}

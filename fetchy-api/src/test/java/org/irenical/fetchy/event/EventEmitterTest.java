package org.irenical.fetchy.event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventEmitterTest {

    private static final String A_TEST_ENTITY = "test-entity";
    private static final String ANOTHER_TEST_ENTITY = "other-test-entity";
    private static final String A_SERVICE_ID = "a-service-id";
    private static final String A_EVENT = "a-event";

    private EventEmitter emitter;

    @SuppressWarnings("rawtypes")
    @Mock
    private Consumer<FetchyEvent> mockConsumer;

    @Before
    public void setUp() throws Exception {
        final ExecutorService mock = mock(ExecutorService.class);

        // Turn everything synchronous
        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(mock).execute(any(Runnable.class));

        emitter = new EventEmitter(mock);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testEventFired() throws Exception {

        emitter.addListener(A_TEST_ENTITY, mockConsumer);
        emitter.fire(A_TEST_ENTITY, A_EVENT, A_SERVICE_ID, null, null, 0);

        ArgumentCaptor<FetchyEvent> captor = ArgumentCaptor.forClass(FetchyEvent.class);
        verify(mockConsumer).accept(captor.capture());

        final FetchyEvent event = captor.getValue();

        Assert.assertNotNull(event);
        Assert.assertEquals(event.getName(), A_EVENT);
        Assert.assertEquals(event.getServiceId(), A_SERVICE_ID);
    }

    @Test
    public void testEventOnlyFiresForCorrectEntity() throws Exception {
        emitter.addListener(A_TEST_ENTITY, mockConsumer);
        emitter.fire(ANOTHER_TEST_ENTITY, A_EVENT, A_SERVICE_ID, null, null, 0);

        verifyZeroInteractions(mockConsumer);
    }

    @Test
    public void testEventIsRemoved() throws Exception {
        final String id = emitter.addListener(A_TEST_ENTITY, mockConsumer);

        emitter.fire(A_TEST_ENTITY, A_EVENT, A_SERVICE_ID, null, null, 0);

        InOrder inOrder = Mockito.inOrder(mockConsumer);
        inOrder.verify(mockConsumer).accept(any(FetchyEvent.class));

        emitter.removeListener(id);
        emitter.fire(A_TEST_ENTITY, A_EVENT, A_SERVICE_ID, null, null, 0);

        inOrder.verifyNoMoreInteractions();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListenerFailsOnMissingEntity() throws Exception {
        emitter.addListener("", mockConsumer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListenerFailsOnMissingConsumer() throws Exception {
        emitter.addListener(A_TEST_ENTITY, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemovingListenerFailsOnMissingId() throws Exception {
        emitter.removeListener("");
    }

    @Test
    public void testClearingAllListeners() throws Exception {
        Assert.assertEquals(0, emitter.getListenerCount());
        Assert.assertEquals(0, emitter.getListenerCount(A_TEST_ENTITY));

        emitter.addListener(A_TEST_ENTITY, mockConsumer);

        Assert.assertEquals(1, emitter.getListenerCount());
        Assert.assertEquals(1, emitter.getListenerCount(A_TEST_ENTITY));

        emitter.clear();

        Assert.assertEquals(0, emitter.getListenerCount());
        Assert.assertEquals(0, emitter.getListenerCount(A_TEST_ENTITY));
    }
}

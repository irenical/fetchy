package org.irenical.fetchy;

import org.irenical.fetchy.event.FetchyEvent;
import org.irenical.fetchy.utils.mock.MockService;
import org.irenical.fetchy.utils.mock.SomethingWrongException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class ListenerTest {

    private long timeout = 100;

    private String serviceId = "serviceId";

    private String callname = "MyCall";

    private Fetchy fetchy = new Fetchy();

    private String output = "Hello";

    private MockService stub = new MockService(output);

    private Node node1 = new Node("http://localhost:1337/api");
    private Node node2 = new Node("http://localhost:1338/api");

    private FetchyEvent<?> event = null;

    private boolean ranListener = false;

    @Before
    public void prepare() {
        fetchy.registerDiscoverer(serviceId, sid -> Arrays.asList(node1, node2));
        fetchy.registerBalancer(serviceId, uris -> uris.get(0));
        fetchy.registerConnector(serviceId, uri -> () -> stub);
    }

    @Test
    public synchronized void testCallOnRequestListenUnlisten() throws InterruptedException {
        String lid = fetchy.onRequest(e -> {
            Assert.assertEquals(serviceId, e.getServiceId());
            Assert.assertEquals(node1, e.getNode());
            Assert.assertEquals(callname, e.getName());
            Assert.assertTrue(e.getElapsedMillis() >= 0);
            Assert.assertTrue(e.getElapsedMillis() < timeout);
            event = e;
            synchronized (ListenerTest.this) {
                ListenerTest.this.notify();
            }
        });

        fetchy.call(serviceId, callname, MockService.class, MockService::getSomething);
        this.wait(timeout);
        Assert.assertNotNull(event);

        event = null;
        fetchy.removeListener(lid);

        fetchy.call(serviceId, callname, MockService.class, MockService::getSomething);
        this.wait(timeout);
        Assert.assertNull(event);
    }

    @Test
    public synchronized void testRunBrokenListener() throws InterruptedException {
        fetchy.onRequest(e -> {
            throw new RuntimeException();
        });
        fetchy.onRequest(e -> {
            ranListener = true;
            synchronized (ListenerTest.this) {
                ListenerTest.this.notify();
            }
        });

        fetchy.run(serviceId, callname, MockService.class, MockService::doSomething);
        this.wait(timeout);
        Assert.assertTrue(ranListener);

    }

    @Test
    public synchronized void testRunOnDiscoverListenUnlisten() throws InterruptedException {
        String lid = fetchy.onDiscover(e -> {
            Assert.assertEquals(serviceId, e.getServiceId());
            Assert.assertEquals(null, e.getNode());
            Assert.assertEquals(callname, e.getName());
            Assert.assertTrue(e.getElapsedMillis() >= 0);
            Assert.assertTrue(e.getElapsedMillis() < timeout);
            Assert.assertEquals(Arrays.asList(node1, node2), e.getTarget());
            event = e;
            synchronized (ListenerTest.this) {
                ListenerTest.this.notify();
            }
        });

        fetchy.run(serviceId, callname, MockService.class, MockService::doSomething);
        this.wait(timeout);
        Assert.assertNotNull(event);

        event = null;
        fetchy.removeListener(lid);

        fetchy.run(serviceId, callname, MockService.class, MockService::doSomething);
        this.wait(timeout);
        Assert.assertNull(event);
    }

    @Test
    public synchronized void testRunOnBalanceListenUnlisten() throws InterruptedException {
        String lid = fetchy.onBalance(e -> {
            Assert.assertEquals(serviceId, e.getServiceId());
            Assert.assertEquals(node1, e.getNode());
            Assert.assertEquals(callname, e.getName());
            Assert.assertTrue(e.getElapsedMillis() >= 0);
            Assert.assertTrue(e.getElapsedMillis() < timeout);
            Assert.assertEquals(node1, e.getTarget());
            event = e;
            synchronized (ListenerTest.this) {
                ListenerTest.this.notify();
            }
        });

        fetchy.run(serviceId, callname, MockService.class, MockService::doSomething);
        this.wait(timeout);
        Assert.assertNotNull(event);

        event = null;
        fetchy.removeListener(lid);

        fetchy.run(serviceId, callname, MockService.class, MockService::doSomething);
        this.wait(timeout);
        Assert.assertNull(event);
    }

    @Test
    public synchronized void testRunOnConnectListenUnlisten() throws InterruptedException {
        String lid = fetchy.onConnect(e -> {
            Assert.assertEquals(serviceId, e.getServiceId());
            Assert.assertEquals(node1, e.getNode());
            Assert.assertEquals(callname, e.getName());
            Assert.assertTrue(e.getElapsedMillis() >= 0);
            Assert.assertTrue(e.getElapsedMillis() < timeout);
            Assert.assertEquals(stub, e.getTarget());
            event = e;
            synchronized (ListenerTest.this) {
                ListenerTest.this.notify();
            }
        });

        fetchy.run(serviceId, callname, MockService.class, MockService::doSomething);
        this.wait(timeout);
        Assert.assertNotNull(event);

        event = null;
        fetchy.removeListener(lid);

        fetchy.run(serviceId, callname, MockService.class, MockService::doSomething);
        this.wait(timeout);
        Assert.assertNull(event);
    }

    @Test
    public synchronized void testRunOnErrorListenUnlisten() throws InterruptedException {
        String lid = fetchy.onError(e -> {
            Assert.assertEquals(serviceId, e.getServiceId());
            Assert.assertEquals(node1, e.getNode());
            Assert.assertEquals(callname, e.getName());
            Assert.assertTrue(e.getElapsedMillis() >= 0);
            Assert.assertTrue(e.getElapsedMillis() < timeout);
            Assert.assertEquals(SomethingWrongException.class, e.getTarget().getClass());
            event = e;
            synchronized (ListenerTest.this) {
                ListenerTest.this.notify();
            }
        });

        try {
            fetchy.run(serviceId, callname, MockService.class, MockService::doSomethingWrong);
        } catch (SomethingWrongException e) {
        }
        this.wait(timeout);
        Assert.assertNotNull(event);

        event = null;
        fetchy.removeListener(lid);

        try {
            fetchy.run(serviceId, callname, MockService.class, MockService::doSomethingWrong);
        } catch (SomethingWrongException e) {
        }
        this.wait(timeout);
        Assert.assertNull(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public synchronized void testListenError() throws InterruptedException {
        fetchy.onRequest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public synchronized void testUnlistenError() throws InterruptedException {
        fetchy.removeListener(null);
    }

}

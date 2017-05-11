package org.irenical.fetchy.request;

import org.irenical.fetchy.Fetchy;
import org.irenical.fetchy.Node;
import org.irenical.fetchy.utils.mock.MockService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class RequestTest {

    private String serviceId = "serviceId";

    private Fetchy fetchy = new Fetchy();

    @Test
    public void testConnectorOnlyCall() {
        String output = "Hello";

        fetchy.registerConnector(serviceId, Node -> () -> new MockService(output));

        String outcome = fetchy.call(serviceId, MockService.class, MockService::getSomething);
        Assert.assertEquals(outcome, output);
    }

    @Test
    public void testSingleNodeNoBalancerCall() {
        String output = "Hello";
        Node correctNode = new Node("http://localhost:1337/api");

        fetchy.registerDiscoverer(serviceId, sid -> Collections.singletonList(correctNode));
        fetchy.registerConnector(serviceId, Node -> () -> new MockService(output));

        String outcome = fetchy.call(serviceId, MockService.class, MockService::getSomething);
        Assert.assertEquals(outcome, output);
    }

    @Test
    public void testDualNodeDefaultBalancerRun() {
        MockService service1 = new MockService(null);
        MockService service2 = new MockService(null);
        Node Node1 = new Node("http://localhost:1337/api");
        Node Node2 = new Node("http://localhost:1336/api");

        fetchy.registerDiscoverer(serviceId, sid -> Arrays.asList(Node1, Node2));
        fetchy.registerConnector(serviceId, Node -> {
            if (Node1.equals(Node)) {
                return () -> service1;
            } else {
                return () -> service2;
            }
        });

        fetchy.run(serviceId, MockService.class, MockService::doSomething);

        Assert.assertTrue(service1.ran);
        Assert.assertFalse(service2.ran);
    }

    @Test
    public void testDualNodeCustomBalancerRun() {
        MockService service1 = new MockService(null);
        MockService service2 = new MockService(null);

        Node Node1 = new Node("http://localhost:1337/api");
        Node Node2 = new Node("http://localhost:1336/api");

        fetchy.registerDiscoverer(serviceId, sid -> Arrays.asList(Node1, Node2));
        fetchy.registerBalancer(serviceId, Nodes -> Nodes.get(1));
        fetchy.registerConnector(serviceId, Node -> {
            if (Node1.equals(Node)) {
                return () -> service1;
            } else {
                return () -> service2;
            }
        });
        fetchy.run(serviceId, MockService.class, MockService::doSomething);
        Assert.assertFalse(service1.ran);
        Assert.assertTrue(service2.ran);
    }

    @Test
    public void testDualNodeCustomBalancerCall() {
        String output1 = "Hello1";
        String output2 = "Hello2";

        MockService service1 = new MockService(output1);
        MockService service2 = new MockService(output2);

        Node Node1 = new Node("http://localhost:1337/api");
        Node Node2 = new Node("http://localhost:1336/api");

        fetchy.registerDiscoverer(serviceId, sid -> Arrays.asList(Node1, Node2));
        fetchy.registerBalancer(serviceId, Nodes -> Nodes.get(1));
        fetchy.registerConnector(serviceId, Node -> {
            if (Node1.equals(Node)) {
                return () -> service1;
            } else {
                return () -> service2;
            }
        });
        String got = fetchy.call(serviceId, MockService.class, MockService::getSomething);
        Assert.assertEquals(output2, got);
    }


    @Test
    public void testConnectorOnlyRun() {
        MockService service = new MockService(null);
        fetchy.registerConnector(serviceId, Node -> () -> service);

        fetchy.run(serviceId, MockService.class, MockService::doSomething);
        Assert.assertTrue(service.ran);
    }

    @Test
    public void testSingleNodeNoBalancerRun() {
        MockService service = new MockService(null);
        Node correctNode = new Node("http://localhost:1337/api");

        fetchy.registerDiscoverer(serviceId, sid -> Collections.singletonList(correctNode));
        fetchy.registerConnector(serviceId, Node -> () -> service);

        fetchy.run(serviceId, MockService.class, MockService::doSomething);
        Assert.assertTrue(service.ran);
    }

    @Test
    public void testRequestBuilder1() throws Exception {
        String output = "Hello";

        fetchy.registerConnector(serviceId, Node -> () -> new MockService(output));

        RequestBuilder<MockService> rb = fetchy.createRequest(serviceId, MockService.class);
        String outcome = rb.callable(MockService::getSomething).build().execute();

        Assert.assertEquals(output, outcome);
    }

    @Test
    public void testRequestBuilder2() throws Exception {
        String output = "Hello";

        fetchy.registerConnector(serviceId, Node -> () -> new MockService(output));

        RequestBuilder<MockService> rb = fetchy.createRequest(serviceId, "callName", MockService.class);
        String outcome = rb.callable(MockService::getSomething).build().execute();

        Assert.assertEquals(output, outcome);
    }

}

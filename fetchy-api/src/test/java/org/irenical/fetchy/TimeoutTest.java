package org.irenical.fetchy;

import org.irenical.fetchy.request.Call;
import org.irenical.fetchy.request.CallableRequest;
import org.irenical.fetchy.request.RequestTimeoutException;
import org.irenical.fetchy.request.Run;
import org.irenical.fetchy.request.RunnableRequest;
import org.irenical.fetchy.utils.mock.MockService;
import org.irenical.fetchy.utils.mock.SomethingWrongException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimeoutTest {

    private long methodRunTime = 10000;

    private int timeout = 100;

    private int maximumAcceptableTimeout = timeout * 2;

    private String output = "Hello";

    private String fallbackOutput = "Bye";

    private String serviceId = "serviceId";

    private MockService service = new MockService(output);

    private Fetchy fetchy = new Fetchy();

    private boolean fellback = false;

    @Before
    public void prepare() {
        fetchy.registerDiscoverer(serviceId, sid -> null);
        fetchy.registerBalancer(serviceId, uris -> null);
        fetchy.registerConnector(serviceId, uri -> () -> service);
    }

    @Test(expected = RequestTimeoutException.class)
    public void testServiceCallTimeout() throws SomethingWrongException {
        long before = System.nanoTime();
        try {
            fetchy.createRequest(serviceId, MockService.class).callable(api -> api.getSomethingSlowly(methodRunTime))
                    .timeout(timeout).build().execute();
        } catch (RequestTimeoutException e) {
            long delta = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - before);
            Assert.assertTrue(delta < maximumAcceptableTimeout);
            throw e;
        }
    }

    @Test(expected = RequestTimeoutException.class)
    public void testServiceRunTimeout() throws SomethingWrongException {
        long before = System.nanoTime();
        try {
            fetchy.createRequest(serviceId, MockService.class).runnable(api -> api.doSomethingSlowly(methodRunTime))
                    .timeout(timeout).build().execute();
        } catch (RequestTimeoutException e) {
            long delta = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - before);
            Assert.assertTrue(delta < maximumAcceptableTimeout);
            throw e;
        }
    }

    @Test
    public void testServiceCallTimein() {
        CallableRequest<String, RuntimeException> cr = fetchy.createRequest(serviceId, MockService.class)
                .callable((Call<String, MockService, RuntimeException>) MockService::getSomething)
                .timeout(timeout).build();
        String got = cr.execute();
        Assert.assertEquals(output, got);
    }

    @Test(expected = SomethingWrongException.class)
    public void testServiceCallTimeinError() throws SomethingWrongException {
        CallableRequest<String, SomethingWrongException> cr = fetchy.createRequest(serviceId, MockService.class)
                .callable(MockService::getSomethingWrong)
                .timeout(timeout).build();
        cr.execute();
    }

    @Test(expected = SomethingWrongException.class)
    public void testServiceRunTimeinError() throws SomethingWrongException {
        RunnableRequest<SomethingWrongException> cr = fetchy.createRequest(serviceId, MockService.class)
                .runnable(MockService::doSomethingWrong)
                .timeout(timeout).build();
        cr.execute();
    }

    @Test
    public void testServiceRunTimein() {
        RunnableRequest<RuntimeException> rr = fetchy.createRequest(serviceId, MockService.class)
                .runnable((Run<MockService, RuntimeException>) MockService::doSomething)
                .timeout(timeout).build();
        rr.execute();
        Assert.assertTrue(service.ran);
    }

    @Test
    public void testFallbackOnServiceCallTimeout() throws SomethingWrongException {
        long before = System.nanoTime();
        String got = fetchy.createRequest(serviceId, MockService.class).callable(api -> api.getSomethingSlowly(methodRunTime))
                .fallback(e -> fallbackOutput).timeout(timeout).build().execute();
        long delta = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - before);
        Assert.assertTrue(delta < maximumAcceptableTimeout);
        Assert.assertEquals(fallbackOutput, got);
    }

    @Test
    public void testFallbackOnServiceRunTimeout() throws SomethingWrongException {
        long before = System.nanoTime();
        fetchy.createRequest(serviceId, MockService.class).runnable(api -> api.doSomethingSlowly(methodRunTime))
                .fallback(e -> {
                    fellback = true;
                }).timeout(timeout).build().execute();
        long delta = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - before);
        Assert.assertTrue(delta < maximumAcceptableTimeout);
        Assert.assertTrue(fellback);
    }

}

package org.irenical.fetchy;

import org.irenical.fetchy.balancer.BalanceException;
import org.irenical.fetchy.connector.ConnectException;
import org.irenical.fetchy.discoverer.DiscoverException;
import org.junit.Assert;
import org.junit.Test;


public class ExceptionObjectsTest {

    private String message = "msg";

    private Exception cause = new Exception();

    @Test
    public void testExceptions() {
        DiscoverException de1 = new DiscoverException(message);
        DiscoverException de2 = new DiscoverException(message, cause);
        BalanceException be1 = new BalanceException(message);
        BalanceException be2 = new BalanceException(message, cause);
        ConnectException ce1 = new ConnectException(message);
        ConnectException ce2 = new ConnectException(message, cause);

        Assert.assertEquals(message, de1.getMessage());
        Assert.assertNull(de1.getCause());
        Assert.assertEquals(message, de2.getMessage());
        Assert.assertEquals(cause, de2.getCause());

        Assert.assertEquals(message, be1.getMessage());
        Assert.assertNull(be1.getCause());
        Assert.assertEquals(message, be2.getMessage());
        Assert.assertEquals(cause, be2.getCause());

        Assert.assertEquals(message, ce1.getMessage());
        Assert.assertNull(ce1.getCause());
        Assert.assertEquals(message, ce2.getMessage());
        Assert.assertEquals(cause, ce2.getCause());

    }

}

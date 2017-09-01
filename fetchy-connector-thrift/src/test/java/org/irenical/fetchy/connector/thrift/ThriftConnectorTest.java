package org.irenical.fetchy.connector.thrift;

import java.util.function.Function;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.irenical.fetchy.Node;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ThriftConnectorTest {
  
  ThriftConnector<MyIFace, MyIFace> connector = new ThriftConnector<>(MyIFace.class);
  
  boolean customTransportBuilderRan = false;
  
  Function<Node, TTransport> customTransportBuilder = node->{
    customTransportBuilderRan = true;
    return Mockito.mock(TTransport.class);
  };

  boolean customProtocolBuilderRan = false;
  
  Function<TTransport, TProtocol> customProtocolBuilder = t->{
    customProtocolBuilderRan = true;
    return Mockito.mock(TProtocol.class);
  };

  boolean customClientBuilderRan = false;
  
  Function<TProtocol, MyIFace> customClientBuilder = p->{
    customClientBuilderRan = true;
    return new MyIFace(p);
  };
  
  @Test
  public void testNoCustomBuildersSet() {
    Assert.assertNull(connector.getTransportBuilder());
    Assert.assertNull(connector.getProtocolBuilder());
    Assert.assertNull(connector.getClientBuilder());
  }
  
  @Test
  public void testTransportCustomBuilderSet() {
    connector.withTransportBuilder(customTransportBuilder);
    Assert.assertTrue(connector.getTransportBuilder() == customTransportBuilder);
    Assert.assertNull(connector.getProtocolBuilder());
    Assert.assertNull(connector.getClientBuilder());
  }
  
  @Test
  public void testProtocolCustomBuilderSet() {
    connector.withProtocolBuilder(customProtocolBuilder);
    Assert.assertTrue(connector.getProtocolBuilder() == customProtocolBuilder);
    
    Assert.assertNull(connector.getTransportBuilder());
    Assert.assertNull(connector.getClientBuilder());
  }
  
  @Test
  public void testClientCustomBuilderSet() {
    connector.withClientBuilder(customClientBuilder);
    Assert.assertTrue(connector.getClientBuilder() == customClientBuilder);
    
    Assert.assertNull(connector.getTransportBuilder());
    Assert.assertNull(connector.getProtocolBuilder());
  }
  
  @Test
  public void testNoCustomBuildersRun() {
    connector.connect(new Node("localhost", 1234));
    Assert.assertFalse(customTransportBuilderRan);
    Assert.assertFalse(customProtocolBuilderRan);
    Assert.assertFalse(customClientBuilderRan);
  }
  
  @Test
  public void testTransportCustomBuilderRun() {
    connector.withTransportBuilder(customTransportBuilder).connect(new Node("localhost", 1234));
    Assert.assertTrue(customTransportBuilderRan);
    Assert.assertFalse(customProtocolBuilderRan);
    Assert.assertFalse(customClientBuilderRan);
  }
  
  @Test
  public void testProtocolCustomBuilderRun() {
    connector.withProtocolBuilder(customProtocolBuilder).connect(new Node("localhost", 1234));
    Assert.assertFalse(customTransportBuilderRan);
    Assert.assertTrue(customProtocolBuilderRan);
    Assert.assertFalse(customClientBuilderRan);
  }
  
  @Test
  public void testClientCustomBuilderRun() {
    connector.withClientBuilder(customClientBuilder).connect(new Node("localhost", 1234));
    Assert.assertFalse(customTransportBuilderRan);
    Assert.assertFalse(customProtocolBuilderRan);
    Assert.assertTrue(customClientBuilderRan);
  }

}

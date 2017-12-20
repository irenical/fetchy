package org.irenical.fetchy.connector.jersey2;

import org.irenical.fetchy.Node;
import org.irenical.fetchy.connector.Stub;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.function.Consumer;

import static org.mockito.Mockito.times;

public class Jersey2ConnectorTest {

    @Test
    public void testNoClientConfigurator() throws Exception {
        Jersey2Connector connector = new Jersey2Connector();

        Stub<WebTarget> connect = connector.connect(new Node( "http://localhost", 1337 ) );

        Assert.assertEquals( "localhost", connect.get().getUri().getHost() );
        Assert.assertEquals( 1337, connect.get().getUri().getPort() );
    }

    @Test
    public void testWithClientConfigurator() throws Exception {
        Consumer<ClientBuilder> clientConfigurator = new Consumer<ClientBuilder>() {
            @Override
            public void accept(ClientBuilder clientBuilder) {

            }
        };

        Consumer<ClientBuilder> clientConfiguratorSpy = Mockito.spy( clientConfigurator );

        Jersey2Connector connector = new Jersey2Connector()
                .withClientConfigurator( clientConfiguratorSpy );

        connector.connect(new Node("localhost", 1337));

        Mockito.verify( clientConfiguratorSpy, times( 1 ) ).accept( Mockito.any() );
    }

}

package org.irenical.fetchy.service.factory.thrift;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.service.factory.ServiceDiscoveryExecutor;

import java.lang.reflect.Constructor;

public class ThriftServiceExecutor< IFACE, CLIENT extends TServiceClient > extends ServiceDiscoveryExecutor< IFACE, CLIENT > {

    private final Class< CLIENT > clientType;

    public ThriftServiceExecutor(Class<CLIENT> clientType, String serviceId) {
        super( serviceId );

        this.clientType = clientType;
    }

    @Override
    protected CLIENT newInstance(ServiceNode serviceNode) throws Exception {
        TTransport tTransport = new TFramedTransport( new TSocket( serviceNode.getAddress(), serviceNode.getPort() ) );
        TProtocol protocol = new TBinaryProtocol( tTransport );
        Constructor< CLIENT > constructor = clientType.getConstructor(TProtocol.class);
        return constructor.newInstance(protocol);
    }

    @Override
    protected void onBeforeExecute(CLIENT client) throws Exception {
        open( client );
    }

    @Override
    protected void onAfterExecute(CLIENT client) {
        close( client );
    }

    private void open( CLIENT clientInstance ) throws TTransportException {
        if ( clientInstance != null ) {
            open(clientInstance.getInputProtocol());
            open(clientInstance.getOutputProtocol());
        }
    }

    private void close( CLIENT clientInstance ) {
        if ( clientInstance != null ) {
            close(clientInstance.getInputProtocol());
            close(clientInstance.getOutputProtocol());
        }
    }

    private void open( TProtocol protocol ) throws TTransportException {
        if ( protocol != null ) {
            TTransport transport = protocol.getTransport();
            if ( transport != null && ! transport.isOpen() ) {
                transport.open();
            }
        }
    }

    private void close( TProtocol protocol ) {
        if ( protocol != null ) {
            TTransport transport = protocol.getTransport();
            if ( transport != null && transport.isOpen() ) {
                transport.close();
            }
        }
    }

}

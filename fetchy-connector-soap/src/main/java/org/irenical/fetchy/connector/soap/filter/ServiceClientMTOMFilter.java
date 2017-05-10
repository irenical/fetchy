package org.irenical.fetchy.connector.soap.filter;

import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;

import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.StreamingAttachmentFeature;
import org.irenical.fetchy.connector.soap.ServiceClient;

public class ServiceClientMTOMFilter implements ServiceClientFilter {

    private static final String TEMP_STORAGE_DIR = System.getProperty("java.io.tmpdir");

    private static final long MIN_FILE_SIZE = 4000000L;

    private static final int CHUNK_SIZE = 128 * 1024;


    public ServiceClientMTOMFilter() {

    }

    @Override
    public < ENDPOINT extends Service, PORT > void init( ServiceClient< ENDPOINT, PORT > context ) {
        MTOMFeature mtom = new MTOMFeature();
        StreamingAttachmentFeature saf = new StreamingAttachmentFeature(TEMP_STORAGE_DIR, true, MIN_FILE_SIZE);

        context.addSOAPFeature(mtom);
        context.addSOAPFeature(saf);
    }

    @Override
    public < ENDPOINT extends Service, PORT > void postGetPort( ServiceClient< ENDPOINT, PORT > context, PORT port ) {
        Map<String, Object> ctxt = ((BindingProvider) port).getRequestContext();
        ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, CHUNK_SIZE);
    }

}

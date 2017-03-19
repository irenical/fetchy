package org.irenical.fetchy.service.factory.soap.filter;

import org.irenical.fetchy.service.factory.soap.ServiceClient;

import javax.xml.ws.Service;

public interface ServiceClientFilter {

    < ENDPOINT extends Service, PORT > void init( ServiceClient< ENDPOINT, PORT > context );

    < ENDPOINT extends Service, PORT > void postGetPort( ServiceClient< ENDPOINT, PORT > context, PORT port );

}

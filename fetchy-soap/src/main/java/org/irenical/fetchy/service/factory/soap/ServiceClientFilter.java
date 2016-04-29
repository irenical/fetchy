package org.irenical.fetchy.service.factory.soap;

import javax.naming.Context;

public abstract class ServiceClientFilter {
    public ServiceClientFilter() {}

    // Must implement this constructor in order to be a valid filter
    public ServiceClientFilter(Context jnidiContext) {}

    public abstract void init(ServiceClient context);

    public abstract void postGetPort(ServiceClient context, Object port);
}

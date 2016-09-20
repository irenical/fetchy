package org.irenical.fetchy.service.factory;


import org.irenical.fetchy.service.ServiceFactory;

public abstract class BaseServiceFactory< IFACE > implements ServiceFactory< IFACE > {

    private final String id;

    public BaseServiceFactory( String id ) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

}

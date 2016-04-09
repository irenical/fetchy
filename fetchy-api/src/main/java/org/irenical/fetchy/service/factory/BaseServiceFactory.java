package org.irenical.fetchy.service.factory;


import org.irenical.fetchy.service.ServiceFactory;

public abstract class BaseServiceFactory< IFACE > implements ServiceFactory< IFACE > {

    private final Class< IFACE > serviceInterface;

    public BaseServiceFactory(Class<IFACE> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Class<IFACE> getServiceInterface() {
        return serviceInterface;
    }

}

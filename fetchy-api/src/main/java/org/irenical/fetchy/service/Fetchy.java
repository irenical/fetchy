package org.irenical.fetchy.service;

import org.irenical.lifecycle.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class Fetchy implements LifeCycle {

  private static final Logger LOG = LoggerFactory.getLogger(Fetchy.class);

  private Map<String, ServiceFactory<?>> factories;

  private Map<String, Stub< ? > > services;

  private void loadFactories() {
    for (ServiceFactory< ? > factory : ServiceLoader.load(ServiceFactory.class)) {
      try {
        register(factory);
      } catch (Exception e) {
        LOG.error("Error registering service, ignoring", e);
      }
    }
  }

  public synchronized < SERVICE > void register(ServiceFactory< SERVICE > factory) throws ServiceAlreadyExistsException {
    if(factory==null){
      LOG.error("Trying to register null service factory, ignoring", new Exception());
      return;
    }
    if(factory.getId()==null){
      LOG.error("Service factory <" + factory + "> must declare service id, ignoring", new Exception());
      return;
    }
    ServiceFactory<?> was = factories.putIfAbsent(factory.getId(), factory);
    if (was != null) {
      throw new ServiceAlreadyExistsException("Error loading service factory " + factory + ". A factory for service " + factory.getId() + " is already registered (" + was + ")");
    }
  }

  public < SERVICE > Optional< Stub< SERVICE > > find( String serviceId, Class< SERVICE > serviceClass ) {
    return find( serviceId );
  }

  @SuppressWarnings("unchecked")
  public < SERVICE > Optional< Stub< SERVICE > > find( String serviceId ) {
    Stub< SERVICE > serviceExecutor = (Stub<SERVICE>) services.get( serviceId );
    if ( serviceExecutor == null ) {
      ServiceFactory<SERVICE> factory = (ServiceFactory<SERVICE>) factories.get( serviceId );
      if (factory != null) {
        synchronized (factory) { // ensure only one service gets instantiated
          serviceExecutor = (Stub<SERVICE>) services.get( serviceId );
          if (serviceExecutor == null) {
            serviceExecutor = factory.createService();
            serviceExecutor.start();
            services.put(serviceId, serviceExecutor);
          }
        }
      }
    }
    return Optional.ofNullable( serviceExecutor );
  }
  
  @Override
  public synchronized void start() {
    factories = new ConcurrentHashMap<>();
    services = new ConcurrentHashMap<>();
    loadFactories();
  }

  @Override
  public synchronized void stop() {
    services.values().forEach(LifeCycle::stop);
    services = null;
  }

  @Override
  public boolean isRunning() {
    return factories != null;
  }

}

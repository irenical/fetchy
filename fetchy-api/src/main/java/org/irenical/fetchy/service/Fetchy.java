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

  private Map<Class<?>, ServiceFactory<?>> factories;

  private Map<Class<?>, Stub< ? > > services;

  private void loadFactories() {
    for (ServiceFactory< ? > factory : ServiceLoader.load(ServiceFactory.class)) {
      try {
        register(factory);
      } catch (ServiceAlreadyExistsException e) {
        LOG.error("Error registering service, ignoring", e);
      }
    }
  }

  public synchronized < SERVICE > void register(ServiceFactory< SERVICE > factory) throws ServiceAlreadyExistsException {
    if(factory==null){
      LOG.error("Trying to register null service factory, ignoring", new Exception());
      return;
    }
    if(factory.getServiceInterface()==null){
      LOG.error("Service factory <" + factory + "> must declare implementing service interface, ignoring", new Exception());
      return;
    }
    ServiceFactory<?> was = factories.putIfAbsent(factory.getServiceInterface(), factory);
    if (was != null) {
      throw new ServiceAlreadyExistsException("Error loading service factory " + factory + ". A factory for service class " + factory.getServiceInterface() + " is already registered (" + was + ")");
    }
  }

  @SuppressWarnings("unchecked")
  public < SERVICE > Optional< Stub< SERVICE > > find( Class< SERVICE > serviceClass ) {
    Stub< SERVICE > serviceExecutor = (Stub<SERVICE>) services.get(serviceClass);
    if ( serviceExecutor == null ) {
      ServiceFactory<SERVICE> factory = (ServiceFactory<SERVICE>) factories.get(serviceClass);
      if (factory != null) {
        synchronized (factory) { // ensure only one service gets instantiated
          serviceExecutor = (Stub<SERVICE>) services.get(serviceClass);
          if (serviceExecutor == null) {
            serviceExecutor = factory.createService();
            serviceExecutor.start();
            services.put(serviceClass, serviceExecutor);
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
    services.values().stream().forEach(LifeCycle::stop);
    services = null;
  }

  @Override
  public boolean isRunning() {
    return factories != null;
  }

}

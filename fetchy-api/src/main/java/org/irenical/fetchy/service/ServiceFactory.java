package org.irenical.fetchy.service;

/**
 * A service factory.
 *
 * @param <SERVICE> the service interface
 */
public interface ServiceFactory< SERVICE > {

  Class< SERVICE > getServiceInterface();

  ServiceExecutor< SERVICE > createService();

}

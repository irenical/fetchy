package org.irenical.fetchy.service;

/**
 * A service factory.
 *
 * @param <SERVICE> the service interface
 */
public interface ServiceFactory< SERVICE > {

  String getId();

  Stub< SERVICE > createService();

}

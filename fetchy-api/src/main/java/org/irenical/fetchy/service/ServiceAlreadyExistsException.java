package org.irenical.fetchy.service;

public class ServiceAlreadyExistsException extends Exception {

  private static final long serialVersionUID = 1L;

  public ServiceAlreadyExistsException() {
    super();
  }

  public ServiceAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ServiceAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }

  public ServiceAlreadyExistsException(String message) {
    super(message);
  }

  public ServiceAlreadyExistsException(Throwable cause) {
    super(cause);
  }
  
}

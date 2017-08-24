package org.irenical.fetchy.request;

@FunctionalInterface
public interface Run<API, ERROR extends Exception> {

    void run(API stub) throws ERROR;

}

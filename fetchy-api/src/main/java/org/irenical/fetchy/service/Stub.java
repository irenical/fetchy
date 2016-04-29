package org.irenical.fetchy.service;

import org.irenical.lifecycle.LifeCycle;

/**
 * @param <IFACE>   the service interface
 */
public interface Stub<IFACE> extends LifeCycle {

    /**
     * @param <IFACE>   the service interface
     * @param <OUTPUT>  the expected output type
     */
    @FunctionalInterface
    interface ServiceCall<IFACE,OUTPUT,ERROR extends Exception> {
        OUTPUT call(IFACE client) throws ERROR;
    }
    
    /**
     * @param <IFACE>   the service interface
     */
    @FunctionalInterface
    interface ServiceRun<IFACE,ERROR extends Exception> {
        void run(IFACE client) throws ERROR;
    }

    <OUTPUT,ERROR extends Exception> OUTPUT call(ServiceCall<IFACE,OUTPUT,ERROR> callable) throws ERROR;
    
}

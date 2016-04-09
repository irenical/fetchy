package org.irenical.fetchy.service;

import org.irenical.lifecycle.LifeCycle;

/**
 * @param <IFACE>   the service interface
 */
public interface ServiceExecutor< IFACE > extends LifeCycle {

    /**
     * @param <IFACE>   the service interface
     * @param <OUTPUT>  the expected output type
     */
    @FunctionalInterface
    interface ServiceCall< IFACE, OUTPUT > {
        OUTPUT call(IFACE client) throws Exception;
    }

    < OUTPUT > OUTPUT execute(ServiceCall<IFACE, OUTPUT> callable) throws Exception;

}

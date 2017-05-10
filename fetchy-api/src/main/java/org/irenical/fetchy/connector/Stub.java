package org.irenical.fetchy.connector;

public interface Stub<API> {

    default void onBeforeExecute() {

    }

    API get();

    default void onAfterExecute() {

    }
}

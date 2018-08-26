package org.rootservices.otter.gateway;


import org.rootservices.otter.security.session.Session;

/**
 * Interface that must be implemented to configure a Otter application.
 *
 * @param <S> Session implementation for application
 * @param <U> User object, intended to be a authenticated user.
 */
public interface Configure<S extends Session, U> {
    void configure(Gateway<S, U> gateway);
    void routes(Gateway<S, U> gateway);
}

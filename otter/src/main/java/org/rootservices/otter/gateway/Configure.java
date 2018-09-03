package org.rootservices.otter.gateway;



/**
 * Interface that must be implemented to configure a Otter application.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public interface Configure<S, U> {
    void configure(Gateway<S, U> gateway);
    void routes(Gateway<S, U> gateway);
}

package org.rootservices.otter.gateway;


import org.rootservices.otter.gateway.entity.Shape;

/**
 * Interface that must be implemented to configure a Otter application.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public interface Configure<S, U> {
    Shape<S> shape();
    void routes(Gateway<S, U> gateway);
}

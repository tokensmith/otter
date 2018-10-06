package org.rootservices.otter.gateway;


import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.security.exception.SessionCtorException;

/**
 * Interface that must be implemented to configure a Otter application.
 *
 */
public interface Configure {
    Shape shape();
    void routes(Gateway gateway) throws SessionCtorException;
}

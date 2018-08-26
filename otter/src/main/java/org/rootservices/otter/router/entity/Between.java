package org.rootservices.otter.router.entity;


import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.session.Session;

/**
 * Implementations will be used as rules that may be run before a request reaches
 * a resource or after a resource executes. Also referred to as a before and a after.
 *
 * @param <S> Session implementation for application
 * @param <U> User object, intended to be a authenticated user.
 */
public interface Between<S extends Session, U> {
    void process(Method method, Request<S, U> request, Response<S> response) throws HaltException;
}

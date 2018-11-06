package org.rootservices.otter.router.entity;


import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.exception.HaltException;

/**
 * Implementations will be used as rules that may be run before a request reaches
 * a resource or after a resource executes. Also referred to as a before and a after.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 * @param <P> Payload object, used for rest requests.
 */
public interface Between<S, U, P> {
    void process(Method method, Request<S, U, P> request, Response<S> response) throws HaltException;
}

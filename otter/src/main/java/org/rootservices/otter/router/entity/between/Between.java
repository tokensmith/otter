package org.rootservices.otter.router.entity.between;


import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;

/**
 * Implementations will be used as rules that may be run before a request reaches
 * a resource or after a resource executes. Also referred to as a before and a after.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public interface Between<S, U> {
    void process(Method method, Request<S, U> request, Response<S> response) throws HaltException;
}

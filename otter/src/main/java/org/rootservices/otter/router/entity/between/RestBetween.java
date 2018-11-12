package org.rootservices.otter.router.entity.between;

import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;


/**
 * Implementations will be used as rules that may be run before a request reaches
 * a resource or after a resource executes. Also referred to as a before and a after.
 *
 * @param <U> User object, intended to be a authenticated user.
 * @param <P> Payload object, the entity for the rest request.
 */
public interface RestBetween<U, P> {
    void process(Method method, RestRequest<U, P> request, RestResponse<P> response) throws HaltException;
}

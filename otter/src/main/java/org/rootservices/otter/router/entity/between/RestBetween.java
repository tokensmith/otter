package org.rootservices.otter.router.entity.between;


import org.rootservices.otter.dispatch.entity.RestBtwnRequest;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;


/**
 * Implementations will be used as rules that may be run before a request reaches
 * a rest resource or after a rest resource executes. Also referred to as a before and a after.
 *
 * @param <U> User object, intended to be a authenticated user.
 */
public interface RestBetween<U> {
    void process(Method method, RestBtwnRequest<U> request, RestBtwnResponse response) throws HaltException;
}

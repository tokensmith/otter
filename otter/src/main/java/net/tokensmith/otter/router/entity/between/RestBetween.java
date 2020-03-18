package net.tokensmith.otter.router.entity.between;


import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.exception.HaltException;


/**
 * Implementations will be used as rules that may be run before a request reaches
 * a rest resource or after a rest resource executes. Also referred to as a before and a after.
 *
 * @param <U> User object, intended to be a authenticated user.
 */
public interface RestBetween<S extends DefaultSession, U> {
    void process(Method method, RestBtwnRequest<U> request, RestBtwnResponse response) throws HaltException;
}

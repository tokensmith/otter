package helper.entity;

import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;

public class DummyBetween<S, U, P> implements Between<S, U, P> {

    @Override
    public void process(Method method, Request<S, U, P> request, Response<S> response) throws HaltException {

    }
}

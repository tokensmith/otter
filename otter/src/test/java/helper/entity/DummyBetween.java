package helper.entity;

import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;

public class DummyBetween<S, U> implements Between<S, U> {

    @Override
    public void process(Method method, Request<S, U> request, Response<S> response) throws HaltException {

    }
}

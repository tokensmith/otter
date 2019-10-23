package helper.entity;

import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.exception.HaltException;

public class DummyBetween<S, U> implements Between<S, U> {

    @Override
    public void process(Method method, Request<S, U> request, Response<S> response) throws HaltException {

    }
}

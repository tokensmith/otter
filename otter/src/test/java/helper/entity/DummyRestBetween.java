package helper.entity;

import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.router.exception.HaltException;

public class DummyRestBetween<U, P> implements RestBetween<U, P> {
    @Override
    public void process(Method method, RestRequest<U, P> request, RestResponse<P> response) throws HaltException {

    }
}

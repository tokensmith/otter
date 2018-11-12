package helper.entity;

import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.router.exception.HaltException;

public class HaltRestBetween implements RestBetween<DummyUser, DummyPayload> {
    @Override
    public void process(Method method, RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) throws HaltException {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        throw new HaltException("Test Halt");
    }
}

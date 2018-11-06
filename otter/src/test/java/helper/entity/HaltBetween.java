package helper.entity;

import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;


public class HaltBetween implements Between<DummySession, DummyUser, EmptyPayload> {
    @Override
    public void process(Method method, Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) throws HaltException {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        throw new HaltException("Test Halt");
    }
}

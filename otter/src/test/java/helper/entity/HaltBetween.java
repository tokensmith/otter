package helper.entity;

import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.between.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;

public class HaltBetween implements Between<DummySession, DummyUser> {
    @Override
    public void process(Method method, Request<DummySession, DummyUser> request, Response<DummySession> response) throws HaltException {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        throw new HaltException("Test Halt");
    }
}

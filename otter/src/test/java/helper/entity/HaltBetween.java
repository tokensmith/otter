package helper.entity;

import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.exception.HaltException;

public class HaltBetween implements Between<DummySession, DummyUser> {
    @Override
    public void process(Method method, Request<DummySession, DummyUser> request, Response<DummySession> response) throws HaltException {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        throw new HaltException("Test Halt");
    }
}

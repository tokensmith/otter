package helper.entity;

import helper.entity.model.DummyUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.entity.RestBtwnRequest;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.router.exception.HaltException;

public class HaltRestBetween implements RestBetween<DummyUser> {
    @Override
    public void process(Method method, RestBtwnRequest<DummyUser> request, RestBtwnResponse response) throws HaltException {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        throw new HaltException("Test Halt");
    }
}

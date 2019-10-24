package helper.entity;

import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;

public class HaltRestBetween implements RestBetween<DummyUser> {
    @Override
    public void process(Method method, RestBtwnRequest<DummyUser> request, RestBtwnResponse response) throws HaltException {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        throw new HaltException("Test Halt");
    }
}

package helper.entity;


import helper.entity.model.DummySession;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;


public class DummyRestBetween<S extends DummySession, U> implements RestBetween<S, U> {

    @Override
    public void process(Method method, RestBtwnRequest<U> request, RestBtwnResponse response) throws HaltException {

    }
}

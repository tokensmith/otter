package helper.entity;


import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;


public class DummyRestBetween<U> implements RestBetween<U> {

    @Override
    public void process(Method method, RestBtwnRequest<U> request, RestBtwnResponse response) throws HaltException {

    }
}

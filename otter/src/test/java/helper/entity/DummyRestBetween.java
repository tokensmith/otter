package helper.entity;


import org.rootservices.otter.dispatch.entity.RestBtwnRequest;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.router.exception.HaltException;


public class DummyRestBetween<U> implements RestBetween<U> {

    @Override
    public void process(Method method, RestBtwnRequest<U> request, RestBtwnResponse response) throws HaltException {

    }
}

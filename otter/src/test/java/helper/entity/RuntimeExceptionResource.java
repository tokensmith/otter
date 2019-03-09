package helper.entity;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;

public class RuntimeExceptionResource extends Resource<DummySession, DummyUser> {
    @Override
    public Response<DummySession> get(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        throw new RuntimeException();
    }

    @Override
    public Response<DummySession> post(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        throw new RuntimeException();
    }

    @Override
    public Response<DummySession> put(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        throw new RuntimeException();
    }

    @Override
    public Response<DummySession> delete(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        throw new RuntimeException();
    }

    @Override
    public Response<DummySession> patch(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        throw new RuntimeException();
    }
}

package helper.entity;

import org.rootservices.otter.controller.ErrorResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;

public class ServerErrorResource extends ErrorResource<DummySession, DummyUser> {
    @Override
    public Response<DummySession> get(Request<DummySession, DummyUser> request, Response<DummySession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }

    @Override
    public Response<DummySession> post(Request<DummySession, DummyUser> request, Response<DummySession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }

    @Override
    public Response<DummySession> put(Request<DummySession, DummyUser> request, Response<DummySession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }

    @Override
    public Response<DummySession> delete(Request<DummySession, DummyUser> request, Response<DummySession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }

    @Override
    public Response<DummySession> patch(Request<DummySession, DummyUser> request, Response<DummySession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }
}

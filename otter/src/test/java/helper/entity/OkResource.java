package helper.entity;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

public class OkResource extends Resource<DummySession, DummyUser, EmptyPayload> {

    @Override
    public Response<DummySession> get(Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> post(Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.CREATED);
        return response;
    }

    @Override
    public Response<DummySession> put(Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> delete(Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> connect(Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> options(Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> trace(Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> patch(Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> head(Request<DummySession, DummyUser, EmptyPayload> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }
}

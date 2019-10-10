package helper.entity;

import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;

public class OkResource extends Resource<DummySession, DummyUser> {

    @Override
    public Response<DummySession> get(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> post(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.CREATED);
        return response;
    }

    @Override
    public Response<DummySession> put(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> delete(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> patch(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> connect(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> options(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> trace(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> head(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }
}

package helper.entity;

import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;

public class ServerErrorResource extends Resource<DummySession, DummyUser> {
    @Override
    public Response<DummySession> get(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }

    @Override
    public Response<DummySession> post(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }

    @Override
    public Response<DummySession> put(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }

    @Override
    public Response<DummySession> delete(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }

    @Override
    public Response<DummySession> patch(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response;
    }
}

package hello.controller;


import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

public class NotFoundResource extends Resource<TokenSession, User> {

    public Response<TokenSession> get(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }
}

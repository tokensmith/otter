package hello.controller;


import hello.security.TokenSession;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

public class NotFoundResource extends Resource<TokenSession> {

    public Response<TokenSession> get(Request<TokenSession> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }
}

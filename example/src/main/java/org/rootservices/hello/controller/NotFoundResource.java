package org.rootservices.hello.controller;


import org.rootservices.hello.security.TokenSession;
import org.rootservices.hello.security.User;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.StatusCode;

public class NotFoundResource extends Resource<TokenSession, User> {

    public Response<TokenSession> get(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }
}

package org.rootservices.hello.controller.html;

import org.rootservices.hello.security.TokenSession;
import org.rootservices.hello.security.User;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.StatusCode;


public class ProtectedResource extends Resource<TokenSession, User> {
    public static String URL = "/protected(.*)";
    private static String JSP_PATH = "/WEB-INF/jsp/protected.jsp";

    @Override
    public Response<TokenSession> get(Request<TokenSession, User> request, Response<TokenSession> response) {

        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, User> request, Response<TokenSession> response) {

        response.setStatusCode(StatusCode.OK);
        return response;
    }
}

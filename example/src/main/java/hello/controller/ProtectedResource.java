package hello.controller;

import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;


public class ProtectedResource extends Resource<TokenSession, User, EmptyPayload> {
    public static String URL = "/protected(.*)";
    private static String JSP_PATH = "/WEB-INF/jsp/protected.jsp";

    @Override
    public Response<TokenSession> get(Request<TokenSession, User, EmptyPayload> request, Response<TokenSession> response) {

        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, User, EmptyPayload> request, Response<TokenSession> response) {

        response.setStatusCode(StatusCode.OK);
        return response;
    }
}

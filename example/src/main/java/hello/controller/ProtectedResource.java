package hello.controller;

import hello.security.TokenSession;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;


public class ProtectedResource extends Resource<TokenSession> {
    public static String URL = "/protected(.*)";
    private static String JSP_PATH = "/WEB-INF/jsp/protected.jsp";

    @Override
    public Response<TokenSession> get(Request<TokenSession> request, Response<TokenSession> response) {

        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession> request, Response<TokenSession> response) {

        response.setStatusCode(StatusCode.OK);
        return response;
    }
}

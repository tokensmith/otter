package hello.controller;


import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.Optional;

public class HelloResource extends Resource<TokenSession, User> {
    public static String URL = "/hello";

    private static String JSP_PATH = "/WEB-INF/jsp/hello.jsp";

    public Response<TokenSession> get(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_PATH));
        return response;
    }
}

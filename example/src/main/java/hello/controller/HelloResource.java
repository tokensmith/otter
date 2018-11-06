package hello.controller;


import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;

import java.util.Optional;

public class HelloResource extends Resource<TokenSession, User, EmptyPayload> {
    public static String URL = "/hello";

    private static String JSP_PATH = "/WEB-INF/jsp/hello.jsp";

    public Response<TokenSession> get(Request<TokenSession, User, EmptyPayload> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_PATH));
        return response;
    }
}

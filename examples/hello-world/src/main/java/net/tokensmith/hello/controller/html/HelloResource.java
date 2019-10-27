package net.tokensmith.hello.controller.html;


import net.tokensmith.hello.security.TokenSession;
import net.tokensmith.hello.security.User;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.entity.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class HelloResource extends Resource<TokenSession, User> {
    protected static Logger LOGGER = LoggerFactory.getLogger(HelloResource.class);
    public static String URL = "/hello";
    private static String JSP_PATH = "/WEB-INF/jsp/hello.jsp";

    public Response<TokenSession> get(Request<TokenSession, User> request, Response<TokenSession> response) {
        LOGGER.info("hello");
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_PATH));
        return response;
    }
}

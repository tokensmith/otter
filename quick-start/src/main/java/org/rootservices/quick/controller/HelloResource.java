package org.rootservices.quick.controller;



import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class HelloResource extends Resource<DefaultSession, DefaultUser> {
    protected static Logger LOGGER = LoggerFactory.getLogger(HelloResource.class);
    public static String URL = "/hello";
    private static String JSP_PATH = "/WEB-INF/jsp/hello.jsp";

    public Response<DefaultSession> get(Request<DefaultSession, DefaultUser> request, Response<DefaultSession> response) {
        LOGGER.info("hello");
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_PATH));
        return response;
    }
}

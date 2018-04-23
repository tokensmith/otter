package hello.controller;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.Optional;

public class HelloResource extends Resource {
    public static String URL = "/hello";
    private static String JSP_PATH = "/WEB-INF/jsp/hello.jsp";

    public Response get(Request request, Response response) {
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_PATH));
        return response;
    }
}

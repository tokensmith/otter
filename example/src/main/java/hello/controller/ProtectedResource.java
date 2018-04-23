package hello.controller;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;


public class ProtectedResource extends Resource {
    public static String URL = "/protected(.*)";
    private static String JSP_PATH = "/WEB-INF/jsp/protected.jsp";

    @Override
    public Response get(Request request, Response response) {

        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response post(Request request, Response response) {

        response.setStatusCode(StatusCode.OK);
        return response;
    }
}

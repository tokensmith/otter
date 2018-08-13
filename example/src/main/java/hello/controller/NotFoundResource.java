package hello.controller;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

public class NotFoundResource extends Resource {

    public Response get(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }
}

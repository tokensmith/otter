package hello.controller.api;

import hello.controller.api.model.ApiUser;
import hello.model.Hello;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;


import java.util.Optional;

public class HelloRestResource extends RestResource<ApiUser, Hello> {
    public static String URL = "/rest/v2/hello";

    @Override
    public RestResponse<Hello> get(RestRequest<ApiUser, Hello> request, RestResponse<Hello> response) {
        response.setStatusCode(StatusCode.OK);

        Hello hello = new Hello("Hello, " + request.getUser().get().getFirstName() + " " + request.getUser().get().getLastName());

        response.setPayload(Optional.of(hello));
        return response;
    }

    @Override
    public RestResponse<Hello> post(RestRequest<ApiUser, Hello> request, RestResponse<Hello> response) {
        response.setStatusCode(StatusCode.CREATED);

        response.setPayload(request.getPayload());

        return response;
    }
}

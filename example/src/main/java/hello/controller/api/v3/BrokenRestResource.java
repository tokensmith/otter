package hello.controller.api.v3;

import hello.controller.api.model.ApiUser;
import hello.controller.api.v3.model.BrokenPayload;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;

public class BrokenRestResource extends RestResource<ApiUser, BrokenPayload> {
    public static String URL = "/rest/v3/broken";

    @Override
    public RestResponse<BrokenPayload> get(RestRequest<ApiUser, BrokenPayload> request, RestResponse<BrokenPayload> response) {
        throw new RuntimeException("unexpected error");
    }

    @Override
    public RestResponse<BrokenPayload> post(RestRequest<ApiUser, BrokenPayload> request, RestResponse<BrokenPayload> response) {
        throw new RuntimeException("unexpected error");
    }

    @Override
    public RestResponse<BrokenPayload> put(RestRequest<ApiUser, BrokenPayload> request, RestResponse<BrokenPayload> response) {
        throw new RuntimeException("unexpected error");
    }

    @Override
    public RestResponse<BrokenPayload> delete(RestRequest<ApiUser, BrokenPayload> request, RestResponse<BrokenPayload> response) {
        throw new RuntimeException("unexpected error");
    }

    @Override
    public RestResponse<BrokenPayload> patch(RestRequest<ApiUser, BrokenPayload> request, RestResponse<BrokenPayload> response) {
        throw new RuntimeException("unexpected error");
    }
}

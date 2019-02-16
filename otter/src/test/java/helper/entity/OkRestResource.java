package helper.entity;

import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;

public class OkRestResource extends RestResource<DummyUser, DummyPayload> {

    @Override
    public RestResponse<DummyPayload> get(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public RestResponse<DummyPayload> post(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.CREATED);
        return response;
    }

    @Override
    public RestResponse<DummyPayload> put(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public RestResponse<DummyPayload> delete(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public RestResponse<DummyPayload> connect(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public RestResponse<DummyPayload> options(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public RestResponse<DummyPayload> trace(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public RestResponse<DummyPayload> patch(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public RestResponse<DummyPayload> head(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }
}

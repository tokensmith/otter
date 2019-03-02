package hello.controller.api.v3.handler;

import hello.controller.api.model.ApiUser;
import hello.controller.api.v3.model.ServerErrorPayload;
import org.rootservices.otter.controller.RestErrorResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;

import java.util.Optional;

public class ServerErrorHandler extends RestErrorResource<ApiUser, ServerErrorPayload> {

    @Override
    public RestResponse<ServerErrorPayload> get(RestErrorRequest<ApiUser> request, RestResponse<ServerErrorPayload> response, Throwable cause) {
        ServerErrorPayload to = to(cause);
        response.setPayload(Optional.of(to));
        response.setStatusCode(StatusCode.SERVER_ERROR);

        return response;
    }

    @Override
    public RestResponse<ServerErrorPayload> post(RestErrorRequest<ApiUser> request, RestResponse<ServerErrorPayload> response, Throwable cause) {
        ServerErrorPayload to = to(cause);
        response.setPayload(Optional.of(to));
        response.setStatusCode(StatusCode.SERVER_ERROR);

        return response;
    }

    @Override
    public RestResponse<ServerErrorPayload> put(RestErrorRequest<ApiUser> request, RestResponse<ServerErrorPayload> response, Throwable cause) {
        ServerErrorPayload to = to(cause);
        response.setPayload(Optional.of(to));
        response.setStatusCode(StatusCode.SERVER_ERROR);

        return response;
    }

    @Override
    public RestResponse<ServerErrorPayload> delete(RestErrorRequest<ApiUser> request, RestResponse<ServerErrorPayload> response, Throwable cause) {
        ServerErrorPayload to = to(cause);
        response.setPayload(Optional.of(to));
        response.setStatusCode(StatusCode.SERVER_ERROR);

        return response;
    }

    @Override
    public RestResponse<ServerErrorPayload> patch(RestErrorRequest<ApiUser> request, RestResponse<ServerErrorPayload> response, Throwable cause) {
        ServerErrorPayload to = to(cause);
        response.setPayload(Optional.of(to));
        response.setStatusCode(StatusCode.SERVER_ERROR);

        return response;
    }

    protected ServerErrorPayload to(Throwable from) {
        ServerErrorPayload to = new ServerErrorPayload("An internal server error occurred.");
        return to;
    }
}

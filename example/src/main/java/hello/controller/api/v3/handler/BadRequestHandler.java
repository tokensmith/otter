package hello.controller.api.v3.handler;

import hello.controller.api.model.ApiUser;
import hello.controller.api.v3.model.BadRequestPayload;
import org.rootservices.otter.controller.RestErrorResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.translator.exception.DeserializationException;
import org.rootservices.otter.translator.exception.Reason;

import java.util.Optional;

public class BadRequestHandler extends RestErrorResource<ApiUser, BadRequestPayload> {

    @Override
    public RestResponse<BadRequestPayload> get(RestErrorRequest<ApiUser> request, RestResponse<BadRequestPayload> response, Throwable cause) {
        BadRequestPayload payload = to(cause);
        response.setPayload(Optional.of(payload));
        response.setStatusCode(StatusCode.BAD_REQUEST);
        return response;
    }

    @Override
    public RestResponse<BadRequestPayload> post(RestErrorRequest<ApiUser> request, RestResponse<BadRequestPayload> response, Throwable cause) {
        BadRequestPayload payload = to(cause);
        response.setPayload(Optional.of(payload));
        response.setStatusCode(StatusCode.BAD_REQUEST);
        return response;
    }

    @Override
    public RestResponse<BadRequestPayload> put(RestErrorRequest<ApiUser> request, RestResponse<BadRequestPayload> response, Throwable cause) {
        BadRequestPayload payload = to(cause);
        response.setPayload(Optional.of(payload));
        response.setStatusCode(StatusCode.BAD_REQUEST);
        return response;
    }

    @Override
    public RestResponse<BadRequestPayload> delete(RestErrorRequest<ApiUser> request, RestResponse<BadRequestPayload> response, Throwable cause) {
        BadRequestPayload payload = to(cause);
        response.setPayload(Optional.of(payload));
        response.setStatusCode(StatusCode.BAD_REQUEST);
        return response;
    }

    @Override
    public RestResponse<BadRequestPayload> patch(RestErrorRequest<ApiUser> request, RestResponse<BadRequestPayload> response, Throwable cause) {BadRequestPayload payload = to(cause);
        response.setPayload(Optional.of(payload));
        response.setStatusCode(StatusCode.BAD_REQUEST);
        return response;
    }

    protected BadRequestPayload to(Throwable from) {
        DeserializationException fromCasted = (DeserializationException) from;
        BadRequestPayload to = new BadRequestPayload();
        to.setMessage("bad request");

        if (Reason.DUPLICATE_KEY.equals(fromCasted.getReason())) {
            to.setKey(fromCasted.getKey().get());
            to.setReason("A key was duplicated in the request body.");
        } else if (Reason.INVALID_VALUE.equals(fromCasted.getReason())) {
            to.setKey(fromCasted.getKey().get());
            to.setReason("There was a invalid value for a key.");
        } else if (Reason.UNKNOWN_KEY.equals(fromCasted.getReason())) {
            to.setKey(fromCasted.getKey().get());
            to.setReason("There was a unexpected key in the request body.");
        } else if (Reason.INVALID_PAYLOAD.equals(fromCasted.getReason())) {
            to.setReason("The payload could not be parsed.");
        } else if (Reason.UNKNOWN.equals(fromCasted.getReason())) {
            to.setReason("A unknown problem occurred parsing request body.");
        }

        return to;
    }
}

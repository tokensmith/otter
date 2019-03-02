package helper.entity;

import org.rootservices.otter.controller.RestErrorResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.translator.exception.DeserializationException;
import org.rootservices.otter.translator.exception.Reason;

import java.util.Optional;

public class ClientErrorRestResource extends RestErrorResource<DummyUser, DummyErrorPayload> {

    public Optional<DummyErrorPayload> errorPayload(Throwable cause) {
        String error = "unknown error";
        String desc = "unknown error";

        if (cause instanceof DeserializationException) {
            DeserializationException castedCause = (DeserializationException) cause;
            if (Reason.INVALID_PAYLOAD.equals(castedCause.getReason())) {
                error = "invalid payload";
                desc = "the payload could not be parsed";
            } else if (Reason.UNKNOWN_KEY.equals(castedCause.getReason())) {
                error = "unknown key";
                desc = String.format("the key, {} was not expected", castedCause.getKey().get());
            } else if (Reason.INVALID_VALUE.equals(castedCause.getReason())) {
                error = "invalid value";
                desc = String.format("the key, {} had a invalid value", castedCause.getKey().get());
            } else if (Reason.DUPLICATE_KEY.equals(castedCause.getReason())) {
                error = "duplicate key";
                desc = String.format("the key, {} was duplicated", castedCause.getKey().get());
            } else if (Reason.UNKNOWN.equals(castedCause.getReason())) {
                error = "unknown error";
                desc = "an unknown error occurred parsing the payload";
            }
        }
        return Optional.of(new DummyErrorPayload(error, desc));
    }
    @Override
    public RestResponse<DummyErrorPayload> get(RestErrorRequest<DummyUser> request, RestResponse<DummyErrorPayload> response, Throwable cause) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<DummyErrorPayload> payload = errorPayload(cause);
        response.setPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> post(RestErrorRequest<DummyUser> request, RestResponse<DummyErrorPayload> response, Throwable cause) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<DummyErrorPayload> payload = errorPayload(cause);
        response.setPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> put(RestErrorRequest<DummyUser> request, RestResponse<DummyErrorPayload> response, Throwable cause) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<DummyErrorPayload> payload = errorPayload(cause);
        response.setPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> delete(RestErrorRequest<DummyUser> request, RestResponse<DummyErrorPayload> response, Throwable cause) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<DummyErrorPayload> payload = errorPayload(cause);
        response.setPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> connect(RestErrorRequest<DummyUser> request, RestResponse<DummyErrorPayload> response, Throwable cause) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<DummyErrorPayload> payload = errorPayload(cause);
        response.setPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> options(RestErrorRequest<DummyUser> request, RestResponse<DummyErrorPayload> response, Throwable cause) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<DummyErrorPayload> payload = errorPayload(cause);
        response.setPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> trace(RestErrorRequest<DummyUser> request, RestResponse<DummyErrorPayload> response, Throwable cause) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<DummyErrorPayload> payload = errorPayload(cause);
        response.setPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> patch(RestErrorRequest<DummyUser> request, RestResponse<DummyErrorPayload> response, Throwable cause) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<DummyErrorPayload> payload = errorPayload(cause);
        response.setPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> head(RestErrorRequest<DummyUser> request, RestResponse<DummyErrorPayload> response, Throwable cause) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<DummyErrorPayload> payload = errorPayload(cause);
        response.setPayload(payload);

        return response;
    }
}

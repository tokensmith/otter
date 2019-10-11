package helper.entity;

import helper.entity.model.AlternatePayload;
import helper.entity.model.DummyErrorPayload;
import helper.entity.model.DummyErrorPayload;
import helper.entity.model.DummyUser;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.ToJsonException;

import java.util.Optional;
import java.util.UUID;

public class RawPayloadErrorRestResource extends RestResource<DummyUser, DummyErrorPayload> {
    private JsonTranslator<AlternatePayload> translator;

    public RawPayloadErrorRestResource(JsonTranslator<AlternatePayload> translator) {
        this.translator = translator;
    }

    protected Optional<byte[]> payload() {
        AlternatePayload alternatePayload = new AlternatePayload(UUID.fromString("b5b24f75-7c7a-453f-a574-1bae3d6820a7"));
        Optional<byte[]> payload = Optional.empty();
        try {
            payload = Optional.of(translator.to(alternatePayload));
        } catch (ToJsonException e) {
            e.printStackTrace();
        }

        return payload;
    }

    @Override
    public RestResponse<DummyErrorPayload> get(RestRequest<DummyUser, DummyErrorPayload> request, RestResponse<DummyErrorPayload> response) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> post(RestRequest<DummyUser, DummyErrorPayload> request, RestResponse<DummyErrorPayload> response) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> put(RestRequest<DummyUser, DummyErrorPayload> request, RestResponse<DummyErrorPayload> response) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> delete(RestRequest<DummyUser, DummyErrorPayload> request, RestResponse<DummyErrorPayload> response) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> connect(RestRequest<DummyUser, DummyErrorPayload> request, RestResponse<DummyErrorPayload> response) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> options(RestRequest<DummyUser, DummyErrorPayload> request, RestResponse<DummyErrorPayload> response) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> trace(RestRequest<DummyUser, DummyErrorPayload> request, RestResponse<DummyErrorPayload> response) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> patch(RestRequest<DummyUser, DummyErrorPayload> request, RestResponse<DummyErrorPayload> response) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyErrorPayload> head(RestRequest<DummyUser, DummyErrorPayload> request, RestResponse<DummyErrorPayload> response) {
        response.setStatusCode(StatusCode.BAD_REQUEST);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }
}

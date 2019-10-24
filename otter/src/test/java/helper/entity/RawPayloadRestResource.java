package helper.entity;

import helper.entity.model.AlternatePayload;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.exception.ToJsonException;

import java.util.Optional;
import java.util.UUID;

public class RawPayloadRestResource extends RestResource<DummyUser, DummyPayload> {
    private JsonTranslator<AlternatePayload> translator;

    public RawPayloadRestResource(JsonTranslator<AlternatePayload> translator) {
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
    public RestResponse<DummyPayload> get(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyPayload> post(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.CREATED);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyPayload> put(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyPayload> delete(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyPayload> connect(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyPayload> options(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyPayload> trace(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyPayload> patch(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }

    @Override
    public RestResponse<DummyPayload> head(RestRequest<DummyUser, DummyPayload> request, RestResponse<DummyPayload> response) {
        response.setStatusCode(StatusCode.OK);

        Optional<byte[]> payload = payload();
        response.setRawPayload(payload);

        return response;
    }
}

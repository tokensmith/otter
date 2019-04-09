package helper.entity;

import org.rootservices.otter.controller.RestErrorResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.translator.exception.DeserializationException;
import org.rootservices.otter.translator.exception.Reason;

import java.util.Optional;

public class ClientErrorRestResource extends RestErrorResource<DummyUser, DummyErrorPayload> {

    @Override
    public Optional<DummyErrorPayload> to(Throwable from) {
        String error = "unknown error";
        String desc = "unknown error";

        if (from instanceof DeserializationException) {
            DeserializationException castedCause = (DeserializationException) from;
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
    public StatusCode statusCode() {
        return StatusCode.BAD_REQUEST;
    }
}

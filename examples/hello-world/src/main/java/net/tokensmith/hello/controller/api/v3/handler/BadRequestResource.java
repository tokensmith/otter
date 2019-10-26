package net.tokensmith.hello.controller.api.v3.handler;

import net.tokensmith.hello.controller.api.model.ApiUser;
import net.tokensmith.hello.controller.api.v3.model.BadRequestPayload;
import net.tokensmith.otter.controller.error.rest.RestErrorResource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.translator.exception.DeserializationException;
import net.tokensmith.otter.translator.exception.Reason;

import java.util.Optional;



public class BadRequestResource extends RestErrorResource<ApiUser, BadRequestPayload> {

    @Override
    public Optional<BadRequestPayload> to(Throwable from) {
        BadRequestPayload to = new BadRequestPayload();
        to.setMessage("bad request");

        if (from.getCause() instanceof DeserializationException) {
            DeserializationException fromCasted = (DeserializationException) from.getCause();

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
        }

        return Optional.of(to);
    }

    @Override
    public StatusCode statusCode() {
        return StatusCode.BAD_REQUEST;
    }
}

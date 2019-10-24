package net.tokensmith.otter.controller.error.rest;


import net.tokensmith.otter.controller.builder.ClientErrorBuilder;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.translator.exception.DeserializationException;
import net.tokensmith.otter.translator.exception.Reason;

import java.util.Optional;


public class BadRequestRestResource<U extends DefaultUser> extends RestErrorResource<U, ClientError> {


    @Override
    public Optional<ClientError> to(Throwable from) {
        Optional<ClientError> to = Optional.empty();

        if (from.getCause() instanceof DeserializationException) {
            ClientErrorBuilder builder = new ClientErrorBuilder();
            builder.source(ClientError.Source.BODY);
            DeserializationException fromCasted = (DeserializationException) from.getCause();

            // 113: should the actual value be added here?
            if (Reason.DUPLICATE_KEY.equals(fromCasted.getReason())) {
                builder.key(fromCasted.getKey().get());
                builder.reason("A key was duplicated in the request body.");
            } else if (Reason.INVALID_VALUE.equals(fromCasted.getReason())) {
                builder.key(fromCasted.getKey().get());
                if (fromCasted.getValue().isPresent()) {
                    builder.actual(fromCasted.getValue().get());
                }
                builder.reason("There was a invalid value for a key.");
            } else if (Reason.UNKNOWN_KEY.equals(fromCasted.getReason())) {
                builder.key(fromCasted.getKey().get());
                builder.reason("There was a unexpected key in the request body.");
            } else if (Reason.INVALID_PAYLOAD.equals(fromCasted.getReason())) {
                builder.reason("The payload could not be parsed.");
            } else if (Reason.UNKNOWN.equals(fromCasted.getReason())) {
                builder.reason("A unknown problem occurred parsing request body.");
            }
            to = Optional.of(builder.build());
        }

        return to;

    }

    @Override
    public StatusCode statusCode() {
        return StatusCode.BAD_REQUEST;
    }
}

package net.tokensmith.otter.controller.error.rest;


import net.tokensmith.otter.controller.builder.ClientErrorBuilder;
import net.tokensmith.otter.controller.entity.Cause;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.json.validator.ValidateError;
import net.tokensmith.otter.dispatch.json.validator.exception.ValidateException;
import net.tokensmith.otter.translator.exception.DeserializationException;
import net.tokensmith.otter.translator.exception.Reason;

import java.util.List;
import java.util.Optional;


public class BadRequestRestResource<U extends DefaultUser> extends RestErrorResource<U, ClientError> {


    @Override
    public Optional<ClientError> to(Throwable from) {
        Optional<ClientError> to = Optional.empty();

        if (from.getCause() instanceof DeserializationException) {
            Cause.Builder causeBuilder = new Cause.Builder();
            causeBuilder.source(Cause.Source.BODY);
            DeserializationException fromCasted = (DeserializationException) from.getCause();

            // 113: should the actual value be added here?
            if (Reason.DUPLICATE_KEY.equals(fromCasted.getReason())) {
                causeBuilder.key(fromCasted.getKey().get());
                causeBuilder.reason("A key was duplicated in the request body.");
            } else if (Reason.INVALID_VALUE.equals(fromCasted.getReason())) {
                causeBuilder.key(fromCasted.getKey().get());
                if (fromCasted.getValue().isPresent()) {
                    causeBuilder.actual(fromCasted.getValue().get());
                }
                causeBuilder.reason("There was a invalid value for a key.");
            } else if (Reason.UNKNOWN_KEY.equals(fromCasted.getReason())) {
                causeBuilder.key(fromCasted.getKey().get());
                causeBuilder.reason("There was a unexpected key in the request body.");
            } else if (Reason.INVALID_PAYLOAD.equals(fromCasted.getReason())) {
                causeBuilder.reason("The payload could not be parsed.");
            } else if (Reason.UNKNOWN.equals(fromCasted.getReason())) {
                causeBuilder.reason("A unknown problem occurred parsing request body.");
            }
            ClientErrorBuilder builder = new ClientErrorBuilder();
            builder.cause(causeBuilder.build());
            to = Optional.of(builder.build());
        } else if (from.getCause() instanceof ValidateException) {
            List<ValidateError> errors = ((ValidateException) from.getCause()).getErrors();

            ClientErrorBuilder builder = new ClientErrorBuilder();
            for(ValidateError error: errors) {
                Cause cause = new Cause.Builder()
                        .key(error.getKey())
                        .actual(error.getValue())
                        .source(Cause.Source.BODY)
                        .reason(error.getDescription())
                        .build();
                builder.cause(cause);
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

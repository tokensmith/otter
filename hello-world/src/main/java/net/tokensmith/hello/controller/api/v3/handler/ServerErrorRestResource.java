package net.tokensmith.hello.controller.api.v3.handler;

import net.tokensmith.hello.controller.api.model.ApiUser;
import net.tokensmith.hello.controller.api.v3.model.ServerErrorPayload;
import net.tokensmith.otter.controller.error.rest.RestErrorResource;
import net.tokensmith.otter.controller.entity.StatusCode;


import java.util.Optional;


public class ServerErrorRestResource extends RestErrorResource<ApiUser, ServerErrorPayload> {

    @Override
    public Optional<ServerErrorPayload> to(Throwable cause) {
        Optional<ServerErrorPayload> to = Optional.of(new ServerErrorPayload("An internal server error occurred."));
        return to;
    }

    @Override
    public StatusCode statusCode() {
        return StatusCode.SERVER_ERROR;
    }
}

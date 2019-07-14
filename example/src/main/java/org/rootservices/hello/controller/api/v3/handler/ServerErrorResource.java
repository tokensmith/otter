package org.rootservices.hello.controller.api.v3.handler;

import org.rootservices.hello.controller.api.model.ApiUser;
import org.rootservices.hello.controller.api.v3.model.ServerErrorPayload;
import org.rootservices.otter.controller.error.RestErrorResource;
import org.rootservices.otter.controller.entity.StatusCode;


import java.util.Optional;


// 113: move this to otter as a default.
public class ServerErrorResource extends RestErrorResource<ApiUser, ServerErrorPayload> {

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

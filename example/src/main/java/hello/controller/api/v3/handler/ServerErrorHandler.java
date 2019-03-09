package hello.controller.api.v3.handler;

import hello.controller.api.model.ApiUser;
import hello.controller.api.v3.model.ServerErrorPayload;
import org.rootservices.otter.controller.RestErrorResource;
import org.rootservices.otter.controller.entity.StatusCode;


import java.util.Optional;

public class ServerErrorHandler extends RestErrorResource<ApiUser, ServerErrorPayload> {

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

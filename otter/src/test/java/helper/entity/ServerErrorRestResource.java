package helper.entity;

import helper.entity.model.DummyErrorPayload;
import helper.entity.model.DummyUser;
import org.rootservices.otter.controller.error.RestErrorResource;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.Optional;

public class ServerErrorRestResource extends RestErrorResource<DummyUser, DummyErrorPayload> {

    @Override
    public Optional<DummyErrorPayload> to(Throwable from) {
        return Optional.empty();
    }

    @Override
    public StatusCode statusCode() {
        return StatusCode.SERVER_ERROR;
    }
}

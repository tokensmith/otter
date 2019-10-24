package net.tokensmith.otter.dispatch.translator;

import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.dispatch.entity.RestErrorRequest;
import net.tokensmith.otter.dispatch.entity.RestErrorResponse;
import net.tokensmith.otter.router.entity.io.Answer;

public interface RestErrorHandler<U extends DefaultUser> {
    Answer run(RestErrorRequest<U> request, RestErrorResponse response, Throwable cause);
}

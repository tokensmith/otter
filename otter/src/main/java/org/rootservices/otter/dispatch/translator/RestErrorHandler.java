package org.rootservices.otter.dispatch.translator;

import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.dispatch.entity.RestErrorResponse;
import org.rootservices.otter.router.entity.io.Answer;

public interface RestErrorHandler<U extends DefaultUser> {
    Answer run(RestErrorRequest<U> request, RestErrorResponse response, Throwable cause);
}

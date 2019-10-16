package org.rootservices.otter.dispatch.json;

import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.translator.RestErrorHandler;
import org.rootservices.otter.dispatch.translator.rest.*;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.DeserializationException;

import java.util.Map;
import java.util.Optional;

/**
 * Used for Dispatch Errors. Look at Engine.java for scenarios this is used.
 * 404, 406, 415
 *
 * @param <U> The user
 * @param <P> The Payload.
 */
public class JsonDispatchErrorRouteRun<U extends DefaultUser, P> extends JsonRouteRun<U, P> {

    public JsonDispatchErrorRouteRun(RestRoute<U, P> restRoute, RestResponseTranslator<P> restResponseTranslator, RestRequestTranslator<U, P> restRequestTranslator, RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator, RestBtwnResponseTranslator<P> restBtwnResponseTranslator, JsonTranslator<P> jsonTranslator, Map<StatusCode, RestErrorHandler<U>> errorHandlers, RestErrorRequestTranslator<U> errorRequestTranslator, RestErrorResponseTranslator errorResponseTranslator) {
        super(restRoute, restResponseTranslator, restRequestTranslator, restBtwnRequestTranslator, restBtwnResponseTranslator, jsonTranslator, errorHandlers, errorRequestTranslator, errorResponseTranslator);
    }

    @Override
    protected Optional<P> to(Optional<byte[]> body) throws DeserializationException {
        return Optional.empty();
    }
}

package net.tokensmith.otter.dispatch.json;

import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.translator.RestErrorHandler;
import net.tokensmith.otter.dispatch.translator.rest.*;
import net.tokensmith.otter.router.entity.RestRoute;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.exception.DeserializationException;

import java.util.Map;
import java.util.Optional;

/**
 * Used for Dispatch Errors. Look at Engine.java for scenarios this is used.
 * 404, 406, 415
 *
 * @param <U> The user
 * @param <P> The Payload.
 */
public class JsonDispatchErrorRouteRun<S extends DefaultSession, U extends DefaultUser, P> extends JsonRouteRun<S, U, P> {

    public JsonDispatchErrorRouteRun(RestRoute<S, U, P> restRoute, RestResponseTranslator<P> restResponseTranslator, RestRequestTranslator<U, P> restRequestTranslator, RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator, RestBtwnResponseTranslator<P> restBtwnResponseTranslator, JsonTranslator<P> jsonTranslator, Map<StatusCode, RestErrorHandler<U>> errorHandlers, RestErrorRequestTranslator<U> errorRequestTranslator, RestErrorResponseTranslator errorResponseTranslator) {
        super(restRoute, restResponseTranslator, restRequestTranslator, restBtwnRequestTranslator, restBtwnResponseTranslator, jsonTranslator, errorHandlers, errorRequestTranslator, errorResponseTranslator);
    }

    @Override
    protected Optional<P> to(Optional<byte[]> body) throws DeserializationException {
        return Optional.empty();
    }
}

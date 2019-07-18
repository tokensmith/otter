package org.rootservices.otter.router.config;


import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.dispatch.JsonErrorHandler;
import org.rootservices.otter.dispatch.JsonRouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.RestErrorHandler;
import org.rootservices.otter.dispatch.translator.rest.*;
import org.rootservices.otter.gateway.entity.rest.RestError;
import org.rootservices.otter.gateway.entity.rest.RestErrorTarget;
import org.rootservices.otter.router.builder.RestRouteBuilder;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.translatable.Translatable;
import org.rootservices.otter.translator.JsonTranslator;

import java.util.HashMap;


public class RouterAppFactory {
    private static OtterAppFactory otterAppFactory = new OtterAppFactory();


    public <U extends DefaultUser, P extends Translatable> RestRoute<U, ? extends Translatable> makeRestRoute(RestErrorTarget<U, P> from) {
        return new RestRouteBuilder<U, P>()
                .restResource(from.getResource())
                .before(from.getBefore())
                .after(from.getAfter())
                .build();
    }

    public <U extends DefaultUser, P extends Translatable> RouteRunner makeJsonRouteRun(RestRoute<U, ? extends Translatable> restRoute, Class<? extends Translatable> payload) {

        Class<P> castedPayload = toPayload(payload);
        JsonTranslator<P> jsonTranslator = otterAppFactory.jsonTranslator(castedPayload);

        RestRequestTranslator<U, P> restRequestTranslator = new RestRequestTranslator<U, P>();
        RestResponseTranslator<P> restResponseTranslator = new RestResponseTranslator<P>();
        RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator = new RestBtwnRequestTranslator<>();
        RestBtwnResponseTranslator<P> restBtwnResponseTranslator = new RestBtwnResponseTranslator<>();

        RestRoute<U, P> castedRestRoute = toRestRoute(restRoute);

        return new JsonRouteRun<U, P>(
                castedRestRoute,
                restResponseTranslator,
                restRequestTranslator,
                restBtwnRequestTranslator,
                restBtwnResponseTranslator,
                jsonTranslator,
                new HashMap<>(),
                new RestErrorRequestTranslator<>(),
                new RestErrorResponseTranslator()
        );
    }

    @SuppressWarnings("unchecked")
    protected <P extends Translatable> Class<P> toPayload(Class<? extends Translatable> from) {
        return (Class<P>) from;
    }

    @SuppressWarnings("unchecked")
    protected <U extends DefaultUser, E extends Translatable> RestRoute<U, E> toRestRoute(RestRoute<U, ? extends Translatable> from) {
        return (RestRoute<U, E>) from;
    }


    public <U extends DefaultUser, P extends Translatable> RestErrorHandler<U> restErrorHandler(RestError<U, ? extends P> restError) {

        RestError<U, P> castedRestErrorValue = toRestError(restError);
        JsonTranslator<P> jsonTranslator = otterAppFactory.jsonTranslator(castedRestErrorValue.getPayload());

        return new JsonErrorHandler<U, P>(
                jsonTranslator,
                castedRestErrorValue.getRestResource(),
                new RestRequestTranslator<>(),
                new RestResponseTranslator<>()
        );
    }

    @SuppressWarnings("unchecked")
    public <U extends DefaultUser, P extends Translatable> RestError<U, P> toRestError(RestError<U, ? extends P> restError) {
        return (RestError<U, P>) restError;
    }

}

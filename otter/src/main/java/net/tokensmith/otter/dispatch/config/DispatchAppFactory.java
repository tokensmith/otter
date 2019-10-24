package net.tokensmith.otter.dispatch.config;


import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.dispatch.json.JsonDispatchErrorRouteRun;
import net.tokensmith.otter.dispatch.json.JsonErrorHandler;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.dispatch.translator.RestErrorHandler;
import net.tokensmith.otter.dispatch.translator.rest.*;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.router.builder.RestRouteBuilder;
import net.tokensmith.otter.router.entity.RestRoute;
import org.rootservices.otter.translatable.Translatable;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.util.HashMap;


public class DispatchAppFactory {
    private static TranslatorAppFactory translatorAppFactory = new TranslatorAppFactory();

    public <U extends DefaultUser, P extends Translatable> RestRoute<U, ? extends Translatable> makeRestRoute(RestErrorTarget<U, P> from) {
        return new RestRouteBuilder<U, P>()
                .restResource(from.getResource())
                .before(from.getBefore())
                .after(from.getAfter())
                .build();
    }

    public <U extends DefaultUser, P extends Translatable> RouteRunner makeJsonDispatchErrorRouteRun(RestRoute<U, ? extends Translatable> restRoute, Class<? extends Translatable> payload) {

        Class<P> castedPayload = toPayload(payload);
        JsonTranslator<P> jsonTranslator = translatorAppFactory.jsonTranslator(castedPayload);

        RestRequestTranslator<U, P> restRequestTranslator = new RestRequestTranslator<U, P>();
        RestResponseTranslator<P> restResponseTranslator = new RestResponseTranslator<P>();
        RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator = new RestBtwnRequestTranslator<>();
        RestBtwnResponseTranslator<P> restBtwnResponseTranslator = new RestBtwnResponseTranslator<>();

        RestRoute<U, P> castedRestRoute = toRestRoute(restRoute);

        return new JsonDispatchErrorRouteRun<>(
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
        JsonTranslator<P> jsonTranslator = translatorAppFactory.jsonTranslator(castedRestErrorValue.getPayload());

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
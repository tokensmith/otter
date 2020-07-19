package net.tokensmith.otter.dispatch.config;


import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.dispatch.json.JsonDispatchErrorRouteRun;
import net.tokensmith.otter.dispatch.json.JsonErrorHandler;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.dispatch.translator.RestErrorHandler;
import net.tokensmith.otter.dispatch.translator.rest.RestBtwnRequestTranslator;
import net.tokensmith.otter.dispatch.translator.rest.RestBtwnResponseTranslator;
import net.tokensmith.otter.dispatch.translator.rest.RestErrorRequestTranslator;
import net.tokensmith.otter.dispatch.translator.rest.RestErrorResponseTranslator;
import net.tokensmith.otter.dispatch.translator.rest.RestRequestTranslator;
import net.tokensmith.otter.dispatch.translator.rest.RestResponseTranslator;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.router.builder.RestRouteBuilder;
import net.tokensmith.otter.router.entity.RestRoute;
import net.tokensmith.otter.translatable.Translatable;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.util.HashMap;


public class DispatchAppFactory {
    private static TranslatorAppFactory translatorAppFactory = new TranslatorAppFactory();

    public <S extends DefaultSession, U extends DefaultUser, P extends Translatable> RestRoute<S, U, ? extends Translatable> makeRestRoute(RestErrorTarget<S, U, P> from) {
        return new RestRouteBuilder<S, U, P>()
                .restResource(from.getResource())
                .before(from.getBefore())
                .after(from.getAfter())
                .build();
    }

    public <U extends DefaultUser, S extends DefaultSession, P extends Translatable> RouteRunner makeJsonDispatchErrorRouteRun(RestRoute<S, U, ? extends Translatable> restRoute, Class<? extends Translatable> payload, Validate validate) {

        Class<P> castedPayload = toPayload(payload);
        JsonTranslator<P> jsonTranslator = translatorAppFactory.jsonTranslator(castedPayload);

        RestRequestTranslator<S, U, P> restRequestTranslator = new RestRequestTranslator<>();
        RestResponseTranslator<P> restResponseTranslator = new RestResponseTranslator<P>();
        RestBtwnRequestTranslator<S, U, P> restBtwnRequestTranslator = new RestBtwnRequestTranslator<>();
        RestBtwnResponseTranslator<P> restBtwnResponseTranslator = new RestBtwnResponseTranslator<>();

        RestRoute<S, U, P> castedRestRoute = toRestRoute(restRoute);

        return new JsonDispatchErrorRouteRun<>(
                castedRestRoute,
                restResponseTranslator,
                restRequestTranslator,
                restBtwnRequestTranslator,
                restBtwnResponseTranslator,
                jsonTranslator,
                validate,
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
    protected <S extends DefaultSession, U extends DefaultUser, E extends Translatable> RestRoute<S, U, E> toRestRoute(RestRoute<S, U, ? extends Translatable> from) {
        return (RestRoute<S, U, E>) from;
    }


    public <S extends DefaultSession, U extends DefaultUser, P extends Translatable> RestErrorHandler<U> restErrorHandler(RestError<U, ? extends P> restError) {

        RestError<U, P> castedRestErrorValue = toRestError(restError);
        JsonTranslator<P> jsonTranslator = translatorAppFactory.jsonTranslator(castedRestErrorValue.getPayload());

        return new JsonErrorHandler<S, U, P>(
                jsonTranslator,
                castedRestErrorValue.getRestResource(),
                new RestRequestTranslator<S, U, P>(),
                new RestResponseTranslator<>()
        );
    }

    @SuppressWarnings("unchecked")
    public <U extends DefaultUser, P extends Translatable> RestError<U, P> toRestError(RestError<U, ? extends P> restError) {
        return (RestError<U, P>) restError;
    }

}

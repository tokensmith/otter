package net.tokensmith.otter.router.builder;


import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.dispatch.json.JsonDispatchErrorRouteRun;
import net.tokensmith.otter.dispatch.json.JsonRouteRun;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.dispatch.translator.RestErrorHandler;
import net.tokensmith.otter.dispatch.translator.rest.*;
import net.tokensmith.otter.router.entity.Location;
import net.tokensmith.otter.router.entity.RestRoute;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class RestLocationBuilder<S extends DefaultSession, U extends DefaultUser, P> {
    private Pattern pattern;
    private List<MimeType> contentTypes = new ArrayList<>();
    private List<MimeType> accepts = new ArrayList<>();
    private RestResource<U, P> restResource;
    private Class<P> payload;
    private List<RestBetween<S, U>> before = new ArrayList<>();
    private List<RestBetween<S, U>> after = new ArrayList<>();
    private Boolean isDispatchError = false;

    // error route runners that are called from engine.
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

    // error resources that will be called from within the routerunner.
    private Map<StatusCode, RestErrorHandler<U>> errorHandlers = new HashMap<>();

    private TranslatorAppFactory translatorAppFactory = new TranslatorAppFactory();

    public RestLocationBuilder<S, U, P> path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public RestLocationBuilder<S, U, P> contentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public RestLocationBuilder<S, U, P> contentType(MimeType contentType) {
        this.contentTypes.add(contentType);
        return this;
    }

    public RestLocationBuilder<S, U, P> accepts(List<MimeType> contentTypes) {
        this.accepts = contentTypes;
        return this;
    }

    public RestLocationBuilder<S, U, P> accept(MimeType contentType) {
        this.accepts.add(contentType);
        return this;
    }

    public RestLocationBuilder<S, U, P> restResource(RestResource<U, P> restResource) {
        this.restResource = restResource;
        return this;
    }

    public RestLocationBuilder<S, U, P> payload(Class<P> payload) {
        this.payload = payload;
        return this;
    }

    public RestLocationBuilder<S, U, P> before(List<RestBetween<S, U>> before) {
        this.before = before;
        return this;
    }

    public RestLocationBuilder<S, U, P> after(List<RestBetween<S, U>> after) {
        this.after = after;
        return this;
    }

    // used in Engine
    public RestLocationBuilder<S, U, P> errorRouteRunner(StatusCode statusCode, RouteRunner errorRouteRunner) {
        errorRouteRunners.put(statusCode, errorRouteRunner);
        return this;
    }

    // used in JsonRouteRun
    public RestLocationBuilder<S, U, P> restErrorHandlers(Map<StatusCode, RestErrorHandler<U>> restErrorHandlers) {

        for(Map.Entry<StatusCode, RestErrorHandler<U>> entry: restErrorHandlers.entrySet()) {
            errorHandlers.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public RestLocationBuilder<S, U, P> isDispatchError(Boolean isDispatchError) {
        this.isDispatchError = isDispatchError;
        return this;
    }

    public Location build() {
        RestRoute<S, U, P> restRoute = new RestRouteBuilder<S, U, P>()
                .restResource(restResource)
                .before(before)
                .after(after)
                .build();

        JsonTranslator<P> jsonTranslator = translatorAppFactory.jsonTranslator(payload);

        RestRequestTranslator<S, U, P> restRequestTranslator = new RestRequestTranslator<S, U, P>();
        RestResponseTranslator<P> restResponseTranslator = new RestResponseTranslator<P>();
        RestBtwnRequestTranslator<S, U, P> restBtwnRequestTranslator = new RestBtwnRequestTranslator<>();
        RestBtwnResponseTranslator<P> restBtwnResponseTranslator = new RestBtwnResponseTranslator<>();

        RouteRunner routeRunner;
        if (isDispatchError) {
            routeRunner = new JsonDispatchErrorRouteRun<>(
                    restRoute,
                    restResponseTranslator,
                    restRequestTranslator,
                    restBtwnRequestTranslator,
                    restBtwnResponseTranslator,
                    jsonTranslator,
                    errorHandlers,
                    new RestErrorRequestTranslator<>(),
                    new RestErrorResponseTranslator()
            );

        } else {
            routeRunner = new JsonRouteRun<S, U, P>(
                    restRoute,
                    restResponseTranslator,
                    restRequestTranslator,
                    restBtwnRequestTranslator,
                    restBtwnResponseTranslator,
                    jsonTranslator,
                    errorHandlers,
                    new RestErrorRequestTranslator<>(),
                    new RestErrorResponseTranslator()
            );
        }

        return new Location(pattern, contentTypes, accepts, routeRunner, errorRouteRunners);
    }
}

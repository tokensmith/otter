package org.rootservices.otter.router.builder;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.JsonRouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.RestErrorHandler;
import org.rootservices.otter.dispatch.translator.rest.*;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.config.TranslatorAppFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class RestLocationBuilder<U extends DefaultUser, P> {
    private Pattern pattern;
    private List<MimeType> contentTypes = new ArrayList<>();
    private List<MimeType> accepts = new ArrayList<>();
    private RestResource<U, P> restResource;
    private Class<P> payload;
    private List<RestBetween<U>> before = new ArrayList<>();
    private List<RestBetween<U>> after = new ArrayList<>();

    // error route runners that are called from engine.
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

    // error resources that will be called from within the routerunner.
    private Map<StatusCode, RestErrorHandler<U>> errorHandlers = new HashMap<>();

    private TranslatorAppFactory translatorAppFactory = new TranslatorAppFactory();

    public RestLocationBuilder<U, P> path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public RestLocationBuilder<U, P> contentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public RestLocationBuilder<U, P> contentType(MimeType contentType) {
        this.contentTypes.add(contentType);
        return this;
    }

    public RestLocationBuilder<U, P> accepts(List<MimeType> contentTypes) {
        this.accepts = contentTypes;
        return this;
    }

    public RestLocationBuilder<U, P> accept(MimeType contentType) {
        this.accepts.add(contentType);
        return this;
    }

    public RestLocationBuilder<U, P> restResource(RestResource<U, P> restResource) {
        this.restResource = restResource;
        return this;
    }

    public RestLocationBuilder<U, P> payload(Class<P> payload) {
        this.payload = payload;
        return this;
    }

    public RestLocationBuilder<U, P> before(List<RestBetween<U>> before) {
        this.before = before;
        return this;
    }

    public RestLocationBuilder<U, P> after(List<RestBetween<U>> after) {
        this.after = after;
        return this;
    }

    // used in Engine
    public RestLocationBuilder<U, P> errorRouteRunner(StatusCode statusCode, RouteRunner errorRouteRunner) {
        errorRouteRunners.put(statusCode, errorRouteRunner);
        return this;
    }

    // used in JsonRouteRun
    public RestLocationBuilder<U, P> restErrorHandlers(Map<StatusCode, RestErrorHandler<U>> restErrorHandlers) {

        for(Map.Entry<StatusCode, RestErrorHandler<U>> entry: restErrorHandlers.entrySet()) {
            errorHandlers.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Location build() {
        RestRoute<U, P> restRoute = new RestRouteBuilder<U, P>()
                .restResource(restResource)
                .before(before)
                .after(after)
                .build();

        JsonTranslator<P> jsonTranslator = translatorAppFactory.jsonTranslator(payload);

        RestRequestTranslator<U, P> restRequestTranslator = new RestRequestTranslator<U, P>();
        RestResponseTranslator<P> restResponseTranslator = new RestResponseTranslator<P>();
        RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator = new RestBtwnRequestTranslator<>();
        RestBtwnResponseTranslator<P> restBtwnResponseTranslator = new RestBtwnResponseTranslator<>();

        RouteRunner routeRunner = new JsonRouteRun<U, P>(
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

        return new Location(pattern, contentTypes, accepts, routeRunner, errorRouteRunners);
    }
}

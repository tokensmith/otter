package org.rootservices.otter.router.builder;

import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.JsonRouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.rest.RestBtwnRequestTranslator;
import org.rootservices.otter.dispatch.translator.rest.RestBtwnResponseTranslator;
import org.rootservices.otter.dispatch.translator.rest.RestRequestTranslator;
import org.rootservices.otter.dispatch.translator.rest.RestResponseTranslator;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;
import org.rootservices.otter.translator.JsonTranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class RestLocationBuilder<U extends DefaultUser, P> {
    private Pattern pattern;
    private List<MimeType> contentTypes = new ArrayList<>();
    private RestResource<U, P> restResource;
    private Class<P> payload;
    private List<RestBetween<U>> before = new ArrayList<>();
    private List<RestBetween<U>> after = new ArrayList<>();
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

    // used when building routeRunner, errorRouteRunners
    private RestRequestTranslator<U, P> restRequestTranslator = new RestRequestTranslator<U, P>();
    private RestResponseTranslator<P> restResponseTranslator = new RestResponseTranslator<P>();
    private RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator = new RestBtwnRequestTranslator<>();
    private RestBtwnResponseTranslator<P> restBtwnResponseTranslator = new RestBtwnResponseTranslator<>();

    private OtterAppFactory otterAppFactory = new OtterAppFactory();

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

    public RestLocationBuilder<U, P> errorRouteRunners(Map<StatusCode, RestRoute<U, P>> errorRoutes) {
        JsonTranslator<P> jsonTranslator = otterAppFactory.jsonTranslator(payload);
        for (Map.Entry<StatusCode, RestRoute<U, P>> entry : errorRoutes.entrySet()) {
            RouteRunner errorRouteRunner = new JsonRouteRun<U, P>(
                    entry.getValue(),
                    restResponseTranslator,
                    restRequestTranslator,
                    restBtwnRequestTranslator,
                    restBtwnResponseTranslator,
                    jsonTranslator
            );
            errorRouteRunners.put(entry.getKey(), errorRouteRunner);
        }
        return this;
    }

    public RestLocationBuilder<U, P> errorRouteRunner(StatusCode statusCode, RestResource<U, P> errorRestResource) {
        JsonTranslator<P> jsonTranslator = otterAppFactory.jsonTranslator(payload);

        RestRoute<U, P> errorRestRoute = new RestRouteBuilder<U, P>()
                .restResource(errorRestResource)
                .build();

        RouteRunner errorRouteRunner = new JsonRouteRun<U, P>(
                errorRestRoute,
                restResponseTranslator,
                restRequestTranslator,
                restBtwnRequestTranslator,
                restBtwnResponseTranslator,
                jsonTranslator
        );
        errorRouteRunners.put(statusCode, errorRouteRunner);
        return this;
    }

    public Location build() {
        RestRoute<U, P> restRoute = new RestRouteBuilder<U, P>()
                .restResource(restResource)
                .before(before)
                .after(after)
                .build();

        JsonTranslator<P> jsonTranslator = otterAppFactory.jsonTranslator(payload);
        RouteRunner routeRunner = new JsonRouteRun<>(
                restRoute,
                restResponseTranslator,
                restRequestTranslator,
                restBtwnRequestTranslator,
                restBtwnResponseTranslator,
                jsonTranslator
        );

        return new Location(pattern, contentTypes, routeRunner, errorRouteRunners);
    }
}

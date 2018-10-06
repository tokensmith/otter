package org.rootservices.otter.router.builder;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.RouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Route;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LocationBuilder<S, U> {
    private Pattern pattern;
    private List<MimeType> contentTypes = new ArrayList<>();
    private Resource<S, U> resource;
    private List<Between<S, U>> before = new ArrayList<>();
    private List<Between<S, U>> after = new ArrayList<>();
    private Map<StatusCode, Route<S, U>> errorRoutes = new HashMap<>();
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

    // used when building routeRunner, errorRouteRunners
    private RequestTranslator<S, U> requestTranslator = new RequestTranslator<>();
    private AnswerTranslator<S> answerTranslator = new AnswerTranslator<>();

    public LocationBuilder<S, U> path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public LocationBuilder<S, U> contentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public LocationBuilder<S, U> contentType(MimeType contentType) {
        this.contentTypes.add(contentType);
        return this;
    }

    public LocationBuilder<S, U> resource(Resource<S, U> resource) {
        this.resource = resource;
        return this;
    }

    public LocationBuilder<S, U> before(List<Between<S, U>> before) {
        this.before = before;
        return this;
    }

    public LocationBuilder<S, U> after(List<Between<S, U>> after) {
        this.after = after;
        return this;
    }

    @Deprecated
    public LocationBuilder<S, U> errorRoutes(Map<StatusCode, Route<S, U>> errorRoutes) {
        this.errorRoutes = errorRoutes;
        return this;
    }

    @Deprecated
    public LocationBuilder<S, U> errorResource(StatusCode statusCode, Resource<S, U> resource) {
        Route<S, U> errorResource = new RouteBuilder<S, U>()
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        this.errorRoutes.put(statusCode, errorResource);
        return this;
    }

    // TODO: rename when integrated.
    public LocationBuilder<S, U> errorRouteRunners(Map<StatusCode, Route<S, U>> errorRoutes) {

        for (Map.Entry<StatusCode, Route<S, U>> entry : errorRoutes.entrySet()) {
            RouteRunner errorRouteRunner = new RouteRun<S, U>(entry.getValue(), requestTranslator, answerTranslator);
            errorRouteRunners.put(entry.getKey(), errorRouteRunner);
        }
        return this;
    }

    public LocationBuilder<S, U> errorRouteRunner(StatusCode statusCode, Resource<S, U> resource) {
        Route<S, U> errorRoute = new RouteBuilder<S, U>()
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        RouteRunner errorRouteRunner = new RouteRun<S, U>(errorRoute, requestTranslator, answerTranslator);
        this.errorRouteRunners.put(statusCode, errorRouteRunner);
        return this;
    }

    public Location<S, U> build() {
        Route<S, U> route = new RouteBuilder<S, U>()
                .resource(resource)
                .before(before)
                .after(after)
                .build();


        RouteRunner routeRunner = new RouteRun<S, U>(route, requestTranslator, answerTranslator);
        return new Location<S, U>(pattern, contentTypes, route, errorRoutes, routeRunner, errorRouteRunners);
    }
}

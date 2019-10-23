package net.tokensmith.otter.router.builder;


import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.dispatch.html.RouteRun;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.dispatch.translator.AnswerTranslator;
import net.tokensmith.otter.dispatch.translator.RequestTranslator;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.entity.Location;
import net.tokensmith.otter.router.entity.Route;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LocationBuilder<S extends DefaultSession, U extends DefaultUser> {
    private Pattern pattern;
    private List<MimeType> contentTypes = new ArrayList<>();
    private List<MimeType> accepts = new ArrayList<>();
    private Resource<S, U> resource;
    private List<Between<S, U>> before = new ArrayList<>();
    private List<Between<S, U>> after = new ArrayList<>();
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();
    private Map<StatusCode, Resource<S, U>> errorResources = new HashMap<>();

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

    public LocationBuilder<S, U> accepts(List<MimeType> contentTypes) {
        this.accepts = contentTypes;
        return this;
    }

    public LocationBuilder<S, U> accept(MimeType contentType) {
        this.accepts.add(contentType);
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

    // used in Engine
    public LocationBuilder<S, U> errorRouteRunners(Map<StatusCode, Route<S, U>> errorRoutes) {

        for (Map.Entry<StatusCode, Route<S, U>> entry : errorRoutes.entrySet()) {
            RouteRunner errorRouteRunner = new RouteRun<S, U>(entry.getValue(), requestTranslator, answerTranslator, errorResources);
            errorRouteRunners.put(entry.getKey(), errorRouteRunner);
        }
        return this;
    }

    // used in Engine
    public LocationBuilder<S, U> errorRouteRunner(StatusCode statusCode, Resource<S, U> resource) {
        Route<S, U> errorRoute = new RouteBuilder<S, U>()
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        RouteRunner errorRouteRunner = new RouteRun<S, U>(errorRoute, requestTranslator, answerTranslator, errorResources);
        this.errorRouteRunners.put(statusCode, errorRouteRunner);
        return this;
    }

    // used in RouteRunner
    public LocationBuilder<S, U> errorResources(Map<StatusCode, Resource<S, U>> errorResources) {
        this.errorResources = errorResources;
        return this;
    }

    public Location build() {
        Route<S, U> route = new RouteBuilder<S, U>()
                .resource(resource)
                .before(before)
                .after(after)
                .build();


        RouteRunner routeRunner = new RouteRun<S, U>(route, requestTranslator, answerTranslator, errorResources);
        return new Location(pattern, contentTypes, accepts, routeRunner, errorRouteRunners);
    }
}

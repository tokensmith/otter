package org.rootservices.otter.router.builder;


import org.rootservices.otter.controller.ErrorResource;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.RouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.entity.between.Between;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Route;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LocationBuilder<S extends DefaultSession, U extends DefaultUser> {
    private Pattern pattern;
    private List<MimeType> contentTypes = new ArrayList<>();
    private Resource<S, U> resource;
    private List<Between<S, U>> before = new ArrayList<>();
    private List<Between<S, U>> after = new ArrayList<>();
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();
    private Map<StatusCode, ErrorResource<S, U>> errorResources = new HashMap<>();

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

    // TODO: 114, 115
    public LocationBuilder<S, U> errorRouteRunners(Map<StatusCode, Route<S, U>> errorRoutes) {

        for (Map.Entry<StatusCode, Route<S, U>> entry : errorRoutes.entrySet()) {
            RouteRunner errorRouteRunner = new RouteRun<S, U>(entry.getValue(), requestTranslator, answerTranslator, errorResources);
            errorRouteRunners.put(entry.getKey(), errorRouteRunner);
        }
        return this;
    }

    // TODO: 114, 115
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

    public LocationBuilder<S, U> errorResources(Map<StatusCode, ErrorResource<S, U>> errorResources) {
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
        return new Location(pattern, contentTypes, routeRunner, errorRouteRunners);
    }
}

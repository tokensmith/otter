package org.rootservices.otter.router.builder;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.HtmlRouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.translatable.Translatable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LocationBuilder<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private Pattern pattern;
    private List<MimeType> contentTypes = new ArrayList<>();
    private Resource<S, U, P> resource;
    private List<Between<S, U, P>> before = new ArrayList<>();
    private List<Between<S, U, P>> after = new ArrayList<>();
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

    // used when building routeRunner, errorRouteRunners
    private RequestTranslator<S, U, P> requestTranslator = new RequestTranslator<>();
    private AnswerTranslator<S> answerTranslator = new AnswerTranslator<>();

    public LocationBuilder<S, U, P> path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public LocationBuilder<S, U, P> contentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public LocationBuilder<S, U, P> contentType(MimeType contentType) {
        this.contentTypes.add(contentType);
        return this;
    }

    public LocationBuilder<S, U, P> resource(Resource<S, U, P> resource) {
        this.resource = resource;
        return this;
    }

    public LocationBuilder<S, U, P> before(List<Between<S, U, P>> before) {
        this.before = before;
        return this;
    }

    public LocationBuilder<S, U, P> after(List<Between<S, U, P>> after) {
        this.after = after;
        return this;
    }

    public LocationBuilder<S, U, P> errorRouteRunners(Map<StatusCode, Route<S, U, P>> errorRoutes) {

        for (Map.Entry<StatusCode, Route<S, U, P>> entry : errorRoutes.entrySet()) {
            RouteRunner errorRouteRunner = new HtmlRouteRun<S, U, P>(entry.getValue(), requestTranslator, answerTranslator);
            errorRouteRunners.put(entry.getKey(), errorRouteRunner);
        }
        return this;
    }

    public LocationBuilder<S, U, P> errorRouteRunner(StatusCode statusCode, Resource<S, U, P> resource) {
        Route<S, U, P> errorRoute = new RouteBuilder<S, U, P>()
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        RouteRunner errorRouteRunner = new HtmlRouteRun<S, U, P>(errorRoute, requestTranslator, answerTranslator);
        this.errorRouteRunners.put(statusCode, errorRouteRunner);
        return this;
    }

    public Location build() {
        Route<S, U, P> route = new RouteBuilder<S, U, P>()
                .resource(resource)
                .before(before)
                .after(after)
                .build();
        
        RouteRunner routeRunner = new HtmlRouteRun<S, U, P>(route, requestTranslator, answerTranslator);
        return new Location(pattern, contentTypes, routeRunner, errorRouteRunners);
    }
}

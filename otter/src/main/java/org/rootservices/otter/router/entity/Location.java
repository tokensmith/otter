package org.rootservices.otter.router.entity;


import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.RouteRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * The target entity to match a http request to.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public class Location<S, U> {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    @Deprecated
    private Route<S, U> route;
    @Deprecated
    private Map<StatusCode, Route<S, U>> errorRoutes;
    private RouteRunner routeRunner;
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

    public Location(Pattern pattern, List<MimeType> contentTypes, Route<S, U> route, Map<StatusCode, Route<S, U>> errorRoutes, RouteRunner routeRunner, Map<StatusCode, RouteRunner> errorRouteRunners) {
        this.pattern = pattern;
        this.contentTypes = contentTypes;
        this.route = route;
        this.errorRoutes = errorRoutes;
        this.routeRunner = routeRunner;
        this.errorRouteRunners = errorRouteRunners;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<MimeType> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public Route<S, U> getRoute() {
        return route;
    }

    public void setRoute(Route<S, U> route) {
        this.route = route;
    }

    public Map<StatusCode, Route<S, U>> getErrorRoutes() {
        return errorRoutes;
    }

    public void setErrorRoutes(Map<StatusCode, Route<S, U>> errorRoutes) {
        this.errorRoutes = errorRoutes;
    }

    public RouteRunner getRouteRunner() {
        return routeRunner;
    }

    public void setRouteRunner(RouteRunner routeRunner) {
        this.routeRunner = routeRunner;
    }

    public Map<StatusCode, RouteRunner> getErrorRouteRunners() {
        return errorRouteRunners;
    }

    public void setErrorRouteRunners(Map<StatusCode, RouteRunner> errorRouteRunners) {
        this.errorRouteRunners = errorRouteRunners;
    }

    @Override
    public String toString() {
        return pattern.toString();
    }
}

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
 */
public class Location {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    private RouteRunner routeRunner;
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

    public Location(Pattern pattern, List<MimeType> contentTypes, RouteRunner routeRunner, Map<StatusCode, RouteRunner> errorRouteRunners) {
        this.pattern = pattern;
        this.contentTypes = contentTypes;
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
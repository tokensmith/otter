package net.tokensmith.otter.router.entity;


import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.dispatch.RouteRunner;

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
    private List<MimeType> accepts;
    private RouteRunner routeRunner;
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

    public Location(Pattern pattern, List<MimeType> contentTypes, List<MimeType> accepts, RouteRunner routeRunner, Map<StatusCode, RouteRunner> errorRouteRunners) {
        this.pattern = pattern;
        this.contentTypes = contentTypes;
        this.accepts = accepts;
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

    public List<MimeType> getAccepts() {
        return accepts;
    }

    public void setAccepts(List<MimeType> accepts) {
        this.accepts = accepts;
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

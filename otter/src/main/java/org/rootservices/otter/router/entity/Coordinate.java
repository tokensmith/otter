package org.rootservices.otter.router.entity;


import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public class Coordinate<S, U> {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    private Route<S, U> route;
    private Map<StatusCode, Route<S, U>> errorRoutes;

    public Coordinate(Pattern pattern, List<MimeType> contentTypes, Route<S, U> route, Map<StatusCode, Route<S, U>> errorRoutes) {
        this.pattern = pattern;
        this.contentTypes = contentTypes;
        this.route = route;
        this.errorRoutes = errorRoutes;
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

    @Override
    public String toString() {
        return pattern.toString();
    }
}

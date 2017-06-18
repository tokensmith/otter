package org.rootservices.otter.router.entity;



import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.RestResource;

import java.util.List;
import java.util.regex.Pattern;


public class Route {
    private Pattern pattern;
    private Resource resource;
    private List<Between> before;
    private List<Between> after;

    public Route(Pattern pattern, Resource resource, List<Between> before, List<Between> after) {
        this.pattern = pattern;
        this.resource = resource;
        this.before = before;
        this.after = after;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public List<Between> getBefore() {
        return before;
    }

    public void setBefore(List<Between> before) {
        this.before = before;
    }

    public List<Between> getAfter() {
        return after;
    }

    public void setAfter(List<Between> after) {
        this.after = after;
    }
}

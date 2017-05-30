package org.rootservices.otter.router.entity;



import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.RestResource;

import java.util.regex.Pattern;


public class Route {
    private Pattern pattern;
    private Resource resource;

    public Route(Pattern pattern, Resource resource) {
        this.pattern = pattern;
        this.resource = resource;
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
}

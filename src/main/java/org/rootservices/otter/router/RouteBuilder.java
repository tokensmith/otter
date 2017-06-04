package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.router.entity.Route;


import java.util.regex.Pattern;

public class RouteBuilder {
    private Pattern pattern;
    private Resource resource;

    public RouteBuilder path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public RouteBuilder resource(Resource resource) {
        this.resource = resource;
        return this;
    }

    public Route build() {
        return new Route(pattern, resource);
    };
}

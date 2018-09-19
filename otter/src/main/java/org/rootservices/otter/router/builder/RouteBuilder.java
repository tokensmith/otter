package org.rootservices.otter.router.builder;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteBuilder<S, U> {
    private Resource<S, U> resource;
    private List<Between<S, U>> before = new ArrayList<>();
    private List<Between<S, U>> after = new ArrayList<>();

    public RouteBuilder<S, U> resource(Resource<S, U> resource) {
        this.resource = resource;
        return this;
    }

    public RouteBuilder<S, U> before(List<Between<S, U>> before) {
        this.before = before;
        return this;
    }

    public RouteBuilder<S, U> after(List<Between<S, U>> after) {
        this.after = after;
        return this;
    }

    public Route<S, U> build() {
        return new Route<S, U>(resource, before, after);
    }
}

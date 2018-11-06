package org.rootservices.otter.router.builder;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.translatable.Translatable;

import java.util.ArrayList;
import java.util.List;

public class RouteBuilder<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private Resource<S, U, P> resource;
    private List<Between<S, U, P>> before = new ArrayList<>();
    private List<Between<S, U, P>> after = new ArrayList<>();

    public RouteBuilder<S, U, P> resource(Resource<S, U, P> resource) {
        this.resource = resource;
        return this;
    }

    public RouteBuilder<S, U, P> before(List<Between<S, U, P>> before) {
        this.before = before;
        return this;
    }

    public RouteBuilder<S, U, P> after(List<Between<S, U, P>> after) {
        this.after = after;
        return this;
    }

    public Route<S, U, P> build() {
        return new Route<S, U, P>(resource, before, after);
    }
}

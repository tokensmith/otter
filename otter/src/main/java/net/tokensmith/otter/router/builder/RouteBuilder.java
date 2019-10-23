package net.tokensmith.otter.router.builder;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.entity.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteBuilder<S extends DefaultSession, U extends DefaultUser> {
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

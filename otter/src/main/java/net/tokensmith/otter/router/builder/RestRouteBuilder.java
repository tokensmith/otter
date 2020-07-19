package net.tokensmith.otter.router.builder;


import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.router.entity.RestRoute;
import net.tokensmith.otter.router.entity.between.RestBetween;

import java.util.ArrayList;
import java.util.List;


public class RestRouteBuilder<S extends DefaultSession, U extends DefaultUser, P> {
    private RestResource<U, P> restResource;
    private List<RestBetween<S, U>> before = new ArrayList<>();
    private List<RestBetween<S, U>> after = new ArrayList<>();

    public RestRouteBuilder<S, U, P> restResource(RestResource<U, P> restResource) {
        this.restResource = restResource;
        return this;
    }

    public RestRouteBuilder<S, U, P> before(List<RestBetween<S, U>> before) {
        this.before = before;
        return this;
    }

    public RestRouteBuilder<S, U, P> after(List<RestBetween<S, U>> after) {
        this.after = after;
        return this;
    }

    public RestRoute<S, U, P> build() {
        return new RestRoute<S, U, P>(restResource, before, after);
    }
}

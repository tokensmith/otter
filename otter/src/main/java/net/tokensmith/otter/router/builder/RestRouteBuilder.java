package net.tokensmith.otter.router.builder;


import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.router.entity.RestRoute;
import net.tokensmith.otter.router.entity.between.RestBetween;


import java.util.ArrayList;
import java.util.List;


public class RestRouteBuilder<U extends DefaultUser, P> {
    private RestResource<U, P> restResource;
    private List<RestBetween<U>> before = new ArrayList<>();
    private List<RestBetween<U>> after = new ArrayList<>();

    public RestRouteBuilder<U, P> restResource(RestResource<U, P> restResource) {
        this.restResource = restResource;
        return this;
    }

    public RestRouteBuilder<U, P> before(List<RestBetween<U>> before) {
        this.before = before;
        return this;
    }

    public RestRouteBuilder<U, P> after(List<RestBetween<U>> after) {
        this.after = after;
        return this;
    }

    public RestRoute<U, P> build() {
        return new RestRoute<U, P>(restResource, before, after);
    }
}

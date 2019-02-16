package org.rootservices.otter.router.builder;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

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

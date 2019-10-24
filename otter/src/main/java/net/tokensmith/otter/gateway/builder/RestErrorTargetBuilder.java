package net.tokensmith.otter.gateway.builder;

import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.router.entity.between.RestBetween;

import java.util.ArrayList;
import java.util.List;


public class RestErrorTargetBuilder<U extends DefaultUser, P> {
    private Class<P> payload;
    private RestResource<U, P> resource;
    private List<RestBetween<U>> before = new ArrayList<>();
    private List<RestBetween<U>> after = new ArrayList<>();

    public RestErrorTargetBuilder<U, P> payload(Class<P> payload) {
        this.payload = payload;
        return this;
    }

    public RestErrorTargetBuilder<U, P> resource(RestResource<U, P> resource) {
        this.resource = resource;
        return this;
    }


    public RestErrorTargetBuilder<U, P> before(List<RestBetween<U>> before) {
        this.before = before;
        return this;
    }

    public RestErrorTargetBuilder<U, P> after(List<RestBetween<U>> after) {
        this.after = after;
        return this;
    }

    public RestErrorTarget<U, P> build() {
        return new RestErrorTarget<U,P>(payload, resource, before, after);
    }
}
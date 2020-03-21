package net.tokensmith.otter.gateway.builder;

import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.router.entity.between.RestBetween;

import java.util.ArrayList;
import java.util.List;


public class RestErrorTargetBuilder<S extends DefaultSession, U extends DefaultUser, P> {
    private Class<P> payload;
    private RestResource<U, P> resource;
    private List<RestBetween<S, U>> before = new ArrayList<>();
    private List<RestBetween<S, U>> after = new ArrayList<>();

    public RestErrorTargetBuilder<S, U, P> payload(Class<P> payload) {
        this.payload = payload;
        return this;
    }

    public RestErrorTargetBuilder<S, U, P> resource(RestResource<U, P> resource) {
        this.resource = resource;
        return this;
    }


    public RestErrorTargetBuilder<S, U, P> before(List<RestBetween<S, U>> before) {
        this.before = before;
        return this;
    }

    public RestErrorTargetBuilder<S, U, P> after(List<RestBetween<S, U>> after) {
        this.after = after;
        return this;
    }

    public RestErrorTarget<S, U, P> build() {
        return new RestErrorTarget<S, U, P>(payload, resource, before, after);
    }
}

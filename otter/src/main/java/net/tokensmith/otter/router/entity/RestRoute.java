package net.tokensmith.otter.router.entity;

import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.router.entity.between.RestBetween;

import java.util.List;

public class RestRoute<S extends DefaultSession, U extends DefaultUser, P> {
    private RestResource<U, P> restResource;
    private List<RestBetween<S, U>> before;
    private List<RestBetween<S, U>> after;

    public RestRoute(RestResource<U, P> restResource, List<RestBetween<S, U>> before, List<RestBetween<S, U>> after) {
        this.restResource = restResource;
        this.before = before;
        this.after = after;
    }

    public RestResource<U, P> getRestResource() {
        return restResource;
    }

    public void setRestResource(RestResource<U, P> restResource) {
        this.restResource = restResource;
    }

    public List<RestBetween<S, U>> getBefore() {
        return before;
    }

    public void setBefore(List<RestBetween<S, U>> before) {
        this.before = before;
    }

    public List<RestBetween<S, U>> getAfter() {
        return after;
    }

    public void setAfter(List<RestBetween<S, U>> after) {
        this.after = after;
    }
}

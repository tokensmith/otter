package net.tokensmith.otter.router.entity;

import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.router.entity.between.RestBetween;

import java.util.List;

public class RestRoute<U extends DefaultUser, P> {
    private RestResource<U, P> restResource;
    private List<RestBetween<U>> before;
    private List<RestBetween<U>> after;

    public RestRoute(RestResource<U, P> restResource, List<RestBetween<U>> before, List<RestBetween<U>> after) {
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

    public List<RestBetween<U>> getBefore() {
        return before;
    }

    public void setBefore(List<RestBetween<U>> before) {
        this.before = before;
    }

    public List<RestBetween<U>> getAfter() {
        return after;
    }

    public void setAfter(List<RestBetween<U>> after) {
        this.after = after;
    }
}

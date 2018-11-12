package org.rootservices.otter.router.entity;

import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;

public class RestRoute<U extends DefaultUser, P extends Translatable> {
    private RestResource<U, P> restResource;
    private List<RestBetween<U, P>> before;
    private List<RestBetween<U, P>> after;

    public RestRoute(RestResource<U, P> restResource, List<RestBetween<U, P>> before, List<RestBetween<U, P>> after) {
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

    public List<RestBetween<U, P>> getBefore() {
        return before;
    }

    public void setBefore(List<RestBetween<U, P>> before) {
        this.before = before;
    }

    public List<RestBetween<U, P>> getAfter() {
        return after;
    }

    public void setAfter(List<RestBetween<U, P>> after) {
        this.after = after;
    }
}

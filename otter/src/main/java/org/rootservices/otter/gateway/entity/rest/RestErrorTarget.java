package org.rootservices.otter.gateway.entity.rest;

import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;

public class RestErrorTarget<U extends DefaultUser, P> {
    private RestResource<U, P> resource;
    private List<RestBetween<U>> before;
    private List<RestBetween<U>> after;

    public RestErrorTarget(RestResource<U, P> resource, List<RestBetween<U>> before, List<RestBetween<U>> after) {
        this.resource = resource;
        this.before = before;
        this.after = after;
    }

    public RestResource<U, P> getResource() {
        return resource;
    }

    public void setResource(RestResource<U, P> resource) {
        this.resource = resource;
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

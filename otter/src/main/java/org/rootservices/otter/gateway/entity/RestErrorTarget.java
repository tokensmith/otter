package org.rootservices.otter.gateway.entity;

import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;

public class RestErrorTarget<U extends DefaultUser, P extends Translatable> {
    private RestResource<U, P> resource;
    private List<RestBetween<U, P>> before;
    private List<RestBetween<U, P>> after;

    public RestErrorTarget(RestResource<U, P> resource, List<RestBetween<U, P>> before, List<RestBetween<U, P>> after) {
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

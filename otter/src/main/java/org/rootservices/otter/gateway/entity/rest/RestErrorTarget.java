package org.rootservices.otter.gateway.entity.rest;

import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;

public class RestErrorTarget<U extends DefaultUser, P> {
    private Class<P> payload;
    private RestResource<U, P> resource;
    private List<RestBetween<U>> before;
    private List<RestBetween<U>> after;

    public RestErrorTarget(Class<P> payload, RestResource<U, P> resource, List<RestBetween<U>> before, List<RestBetween<U>> after) {
        this.payload = payload;
        this.resource = resource;
        this.before = before;
        this.after = after;
    }

    public Class<P> getPayload() {
        return payload;
    }

    public void setPayload(Class<P> payload) {
        this.payload = payload;
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

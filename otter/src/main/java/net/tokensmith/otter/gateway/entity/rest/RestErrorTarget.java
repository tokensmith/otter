package net.tokensmith.otter.gateway.entity.rest;

import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.router.entity.between.RestBetween;

import java.util.List;

public class RestErrorTarget<S extends DefaultSession, U extends DefaultUser, P> {
    private Class<P> payload;
    private RestResource<U, P> resource;
    private List<RestBetween<S, U>> before;
    private List<RestBetween<S, U>> after;

    public RestErrorTarget(Class<P> payload, RestResource<U, P> resource, List<RestBetween<S, U>> before, List<RestBetween<S, U>> after) {
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

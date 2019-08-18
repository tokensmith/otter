package org.rootservices.otter.gateway.entity.rest;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.translatable.Translatable;

public class RestError<U extends DefaultUser, P extends Translatable> {
    private Class<P> payload;
    private RestResource<U, P> restErrorResource;

    public RestError(Class<P> payload, RestResource<U, P> restErrorResource) {
        this.payload = payload;
        this.restErrorResource = restErrorResource;
    }

    public Class<P> getPayload() {
        return payload;
    }

    public void setPayload(Class<P> payload) {
        this.payload = payload;
    }

    public RestResource<U, P> getRestResource() {
        return restErrorResource;
    }

    public void setRestResource(RestResource<U, P> restErrorResource) {
        this.restErrorResource = restErrorResource;
    }
}

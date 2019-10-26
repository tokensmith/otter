package net.tokensmith.otter.gateway.entity.rest;


import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.translatable.Translatable;

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

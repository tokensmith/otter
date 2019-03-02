package org.rootservices.otter.gateway.entity.rest;

import org.rootservices.otter.controller.RestErrorResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.translatable.Translatable;

public class RestError<U extends DefaultUser, P extends Translatable> {
    private Class<P> payload;
    private RestErrorResource<U, P> restErrorResource;

    public RestError(Class<P> payload, RestErrorResource<U, P> restErrorResource) {
        this.payload = payload;
        this.restErrorResource = restErrorResource;
    }

    public Class<P> getPayload() {
        return payload;
    }

    public void setPayload(Class<P> payload) {
        this.payload = payload;
    }

    public RestErrorResource<U, P> getRestErrorResource() {
        return restErrorResource;
    }

    public void setRestErrorResource(RestErrorResource<U, P> restErrorResource) {
        this.restErrorResource = restErrorResource;
    }
}

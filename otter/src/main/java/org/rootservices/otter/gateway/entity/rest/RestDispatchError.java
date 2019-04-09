package org.rootservices.otter.gateway.entity.rest;

import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.translatable.Translatable;

public class RestDispatchError<U extends DefaultUser, P extends Translatable> {
    private Class<P> payload;
    private RestResource<U, P> restDispatchErrorResource;

    public RestDispatchError(Class<P> payload, RestResource<U, P> restDispatchErrorResource) {
        this.payload = payload;
        this.restDispatchErrorResource = restDispatchErrorResource;
    }

    public Class<P> getPayload() {
        return payload;
    }

    public void setPayload(Class<P> payload) {
        this.payload = payload;
    }

    public RestResource<U, P> getRestDispatchErrorResource() {
        return restDispatchErrorResource;
    }

    public void setRestDispatchErrorResource(RestResource<U, P> restDispatchErrorResource) {
        this.restDispatchErrorResource = restDispatchErrorResource;
    }
}

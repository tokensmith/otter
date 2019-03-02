package org.rootservices.otter.gateway.builder;

import org.rootservices.otter.controller.RestErrorResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.gateway.entity.rest.RestError;
import org.rootservices.otter.gateway.entity.rest.RestGroup;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RestGroupBuilder<U extends DefaultUser> {
    private String name;
    private Optional<RestBetween<U>> authRequired = Optional.empty();
    private Optional<RestBetween<U>> authOptional = Optional.empty();

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors = new HashMap<>();

    public RestGroupBuilder<U> name(String name) {
        this.name = name;
        return this;
    }

    public RestGroupBuilder<U> authRequired(RestBetween<U> authRequired) {
        this.authRequired = Optional.of(authRequired);
        return this;
    }

    public RestGroupBuilder<U> authOptional(RestBetween<U> authOptional) {
        this.authOptional = Optional.of(authOptional);
        return this;
    }

    public <P extends Translatable> RestGroupBuilder<U> errorRoute(StatusCode statusCode, RestErrorResource<U, P> restErrorResource, Class<P> errorPayload) {
        RestError<U, P> restError = new RestError<>(errorPayload, restErrorResource);
        restErrors.put(statusCode, restError);
        return this;
    }

    public RestGroup<U> build() {
        return new RestGroup<>(name, authRequired, authOptional, restErrors);
    }
}

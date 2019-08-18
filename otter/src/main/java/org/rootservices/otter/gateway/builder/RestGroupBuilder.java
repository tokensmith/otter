package org.rootservices.otter.gateway.builder;

import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.gateway.entity.rest.RestError;
import org.rootservices.otter.gateway.entity.rest.RestErrorTarget;
import org.rootservices.otter.gateway.entity.rest.RestGroup;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class RestGroupBuilder<U extends DefaultUser> {
    private String name;
    private Optional<RestBetween<U>> authRequired = Optional.empty();
    private Optional<RestBetween<U>> authOptional = Optional.empty();

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors = new HashMap<>();
    // for engine to handle errors
    private Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> dispatchErrors = new HashMap<>();

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

    public <P extends Translatable> RestGroupBuilder<U> onError(StatusCode statusCode, RestResource<U, P> restResource, Class<P> errorPayload) {
        RestError<U, P> restError = new RestError<>(errorPayload, restResource);
        restErrors.put(statusCode, restError);
        return this;
    }

    public <P extends Translatable> RestGroupBuilder<U> onDispatchError(StatusCode statusCode, RestErrorTarget<U, P> dispatchError) {
        this.dispatchErrors.put(statusCode, dispatchError);
        return this;
    }

    public RestGroup<U> build() {
        return new RestGroup<>(name, authRequired, authOptional, restErrors, dispatchErrors);
    }
}

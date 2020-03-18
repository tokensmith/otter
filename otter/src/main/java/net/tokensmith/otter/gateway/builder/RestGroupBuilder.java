package net.tokensmith.otter.gateway.builder;

import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.entity.rest.RestGroup;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.translatable.Translatable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RestGroupBuilder<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Optional<RestBetween<S, U>> authRequired = Optional.empty();
    private Optional<RestBetween<S, U>> authOptional = Optional.empty();

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors = new HashMap<>();
    // for engine to handle errors
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors = new HashMap<>();

    public RestGroupBuilder<S, U> name(String name) {
        this.name = name;
        return this;
    }

    public RestGroupBuilder<S, U> authRequired(RestBetween<S, U> authRequired) {
        this.authRequired = Optional.of(authRequired);
        return this;
    }

    public RestGroupBuilder<S, U> authOptional(RestBetween<S, U> authOptional) {
        this.authOptional = Optional.of(authOptional);
        return this;
    }

    public <P extends Translatable> RestGroupBuilder<S, U> onError(StatusCode statusCode, RestResource<U, P> restResource, Class<P> errorPayload) {
        RestError<U, P> restError = new RestError<>(errorPayload, restResource);
        restErrors.put(statusCode, restError);
        return this;
    }

    public <P extends Translatable> RestGroupBuilder<S, U> onDispatchError(StatusCode statusCode, RestErrorTarget<S, U, P> dispatchError) {
        this.dispatchErrors.put(statusCode, dispatchError);
        return this;
    }

    public RestGroup<S, U> build() {
        return new RestGroup<>(name, authRequired, authOptional, restErrors, dispatchErrors);
    }
}

package org.rootservices.otter.gateway.builder;

import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.gateway.entity.RestGroup;
import org.rootservices.otter.router.entity.between.RestBetween;

import java.util.Optional;

public class RestGroupBuilder<U extends DefaultUser> {
    private String name;
    private Optional<RestBetween<U>> authRequired = Optional.empty();
    private Optional<RestBetween<U>> authOptional = Optional.empty();

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

    public RestGroup<U> build() {
        return new RestGroup<>(name, authRequired, authOptional);
    }
}

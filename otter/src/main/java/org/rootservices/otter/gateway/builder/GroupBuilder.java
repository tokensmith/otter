package org.rootservices.otter.gateway.builder;

import org.rootservices.otter.controller.ErrorResource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.router.entity.between.Between;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class GroupBuilder<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Class<S> sessionClazz;
    private Between<S, U> authRequired;
    private Between<S, U> authOptional;
    private Map<StatusCode, ErrorResource<S, U>> errorResources = new HashMap<>();

    public GroupBuilder<S, U> name(String name) {
        this.name = name;
        return this;
    }

    public GroupBuilder<S, U> sessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
        return this;
    }

    public GroupBuilder<S, U> authRequired(Between<S, U> authRequired) {
        this.authRequired = authRequired;
        return this;
    }

    public GroupBuilder<S, U> authOptional(Between<S, U> authOptional) {
        this.authOptional = authOptional;
        return this;
    }

    public GroupBuilder<S, U> errorResource(StatusCode statusCode, ErrorResource<S, U> errorResource) {
        this.errorResources.put(statusCode, errorResource);
        return this;
    }

    public Group<S, U> build() {
        return new Group<S, U>(name, sessionClazz, makeBetween(authRequired), makeBetween(authOptional), errorResources);
    }

    protected Optional<Between<S, U>> makeBetween(Between<S, U> between) {
        if (between == null) {
            return Optional.empty();
        } else {
            return Optional.of(between);
        }
    }
}

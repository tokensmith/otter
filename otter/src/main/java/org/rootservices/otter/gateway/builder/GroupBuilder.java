package org.rootservices.otter.gateway.builder;

import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.translatable.Translatable;

import java.util.Optional;


public class GroupBuilder<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private String name;
    private Class<S> sessionClazz;
    private Between<S, U, P> authRequired;
    private Between<S, U, P> authOptional;

    public GroupBuilder<S, U, P> name(String name) {
        this.name = name;
        return this;
    }

    public GroupBuilder<S, U, P> sessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
        return this;
    }

    public GroupBuilder<S, U, P> authRequired(Between<S, U, P> authRequired) {
        this.authRequired = authRequired;
        return this;
    }

    public GroupBuilder<S, U, P> authOptional(Between<S, U, P> authOptional) {
        this.authOptional = authOptional;
        return this;
    }

    public Group<S, U, P> build() {
        return new Group<S, U, P>(name, sessionClazz, makeBetween(authRequired), makeBetween(authOptional));
    }

    protected Optional<Between<S, U, P>> makeBetween(Between<S, U, P> between) {
        if (between == null) {
            return Optional.empty();
        } else {
            return Optional.of(between);
        }
    }
}

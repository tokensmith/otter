package net.tokensmith.otter.gateway.builder;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.gateway.entity.Group;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.between.Between;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class GroupBuilder<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Class<S> sessionClazz;

    private Map<Label, List<Between<S, U>>> before = new HashMap<>();
    private Map<Label, List<Between<S, U>>> after = new HashMap<>();

    private Between<S, U> authRequired;
    private Between<S, U> authOptional;
    private Map<StatusCode, Resource<S, U>> errorResources = new HashMap<>();
    private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors = new HashMap<>();

    public GroupBuilder<S, U> name(String name) {
        this.name = name;
        return this;
    }

    public GroupBuilder<S, U> sessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
        return this;
    }

    public GroupBuilder<S, U> before(Label label, Between<S, U> before) {
        if (Objects.isNull(this.before.get(label))) {
            this.before.put(label, new ArrayList<>());
        }
        this.before.get(label).add(before);
        return this;
    }

    public GroupBuilder<S, U> after(Label label, Between<S, U> after) {
        if (Objects.isNull(this.after.get(label))) {
            this.after.put(label, new ArrayList<>());
        }
        this.after.get(label).add(after);
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

    public GroupBuilder<S, U> onError(StatusCode statusCode, Resource<S, U> errorResource) {
        this.errorResources.put(statusCode, errorResource);
        return this;
    }

    public GroupBuilder<S, U> onDispatchError(StatusCode statusCode, ErrorTarget<S, U> dispatchError) {
        this.dispatchErrors.put(statusCode, dispatchError);
        return this;
    }

    public Group<S, U> build() {
        return new Group<S, U>(
                name,
                sessionClazz,
                makeBetween(authRequired),
                makeBetween(authOptional),
                before,
                after,
                errorResources,
                dispatchErrors
        );
    }

    protected Optional<Between<S, U>> makeBetween(Between<S, U> between) {
        if (between == null) {
            return Optional.empty();
        } else {
            return Optional.of(between);
        }
    }
}

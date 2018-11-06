package org.rootservices.otter.gateway.builder;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.translatable.Translatable;


import java.util.ArrayList;
import java.util.List;

public class ErrorTargetBuilder<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private Resource<S, U, P> resource;
    private List<Between<S, U, P>> before = new ArrayList<>();
    private List<Between<S, U, P>> after = new ArrayList<>();

    public ErrorTargetBuilder<S, U, P> resource(Resource<S, U, P> resource) {
        this.resource = resource;
        return this;
    }

    public ErrorTargetBuilder<S, U, P> before(Between<S, U, P> before) {
        this.before.add(before);
        return this;
    }

    public ErrorTargetBuilder<S, U, P> after(Between<S, U, P> after) {
        this.after.add(after);
        return this;
    }

    public ErrorTarget<S, U, P> build() {
        return new ErrorTarget<S, U, P>(resource, before, after);
    }
}

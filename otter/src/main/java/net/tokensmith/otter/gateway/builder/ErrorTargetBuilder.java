package net.tokensmith.otter.gateway.builder;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.router.entity.between.Between;


import java.util.ArrayList;
import java.util.List;

public class ErrorTargetBuilder<S extends DefaultSession, U extends DefaultUser> {
    private Resource<S, U> resource;
    private List<Between<S, U>> before = new ArrayList<>();
    private List<Between<S, U>> after = new ArrayList<>();

    public ErrorTargetBuilder<S, U> resource(Resource<S, U> resource) {
        this.resource = resource;
        return this;
    }

    public ErrorTargetBuilder<S, U> before(Between<S, U> before) {
        this.before.add(before);
        return this;
    }

    public ErrorTargetBuilder<S, U> after(Between<S, U> after) {
        this.after.add(after);
        return this;
    }

    public ErrorTarget<S, U> build() {
        return new ErrorTarget<S, U>(resource, before, after);
    }
}

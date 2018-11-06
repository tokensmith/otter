package org.rootservices.otter.router.entity;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;

public class Route<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private Resource<S, U, P> resource;
    private List<Between<S, U, P>> before;
    private List<Between<S, U, P>> after;

    public Route(Resource<S, U, P> resource, List<Between<S, U, P>> before, List<Between<S, U, P>> after) {
        this.resource = resource;
        this.before = before;
        this.after = after;
    }

    public Resource<S, U, P> getResource() {
        return resource;
    }

    public void setResource(Resource<S, U, P> resource) {
        this.resource = resource;
    }

    public List<Between<S, U, P>> getBefore() {
        return before;
    }

    public void setBefore(List<Between<S, U, P>> before) {
        this.before = before;
    }

    public List<Between<S, U, P>> getAfter() {
        return after;
    }

    public void setAfter(List<Between<S, U, P>> after) {
        this.after = after;
    }
}

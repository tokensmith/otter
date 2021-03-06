package net.tokensmith.otter.gateway.entity;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.router.entity.between.Between;

import java.util.List;

public class ErrorTarget<S extends DefaultSession, U extends DefaultUser> {
    private Resource<S, U> resource;
    private List<Between<S, U>> before;
    private List<Between<S, U>> after;

    public ErrorTarget(Resource<S, U> resource, List<Between<S, U>> before, List<Between<S, U>> after) {
        this.resource = resource;
        this.before = before;
        this.after = after;
    }

    public Resource<S, U> getResource() {
        return resource;
    }

    public void setResource(Resource<S, U> resource) {
        this.resource = resource;
    }

    public List<Between<S, U>> getBefore() {
        return before;
    }

    public void setBefore(List<Between<S, U>> before) {
        this.before = before;
    }

    public List<Between<S, U>> getAfter() {
        return after;
    }

    public void setAfter(List<Between<S, U>> after) {
        this.after = after;
    }
}

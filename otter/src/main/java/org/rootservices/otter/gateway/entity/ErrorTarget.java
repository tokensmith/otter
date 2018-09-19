package org.rootservices.otter.gateway.entity;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.router.entity.Between;

import java.util.List;

public class ErrorTarget<S, U> {
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

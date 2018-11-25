package org.rootservices.otter.security.builder.entity;

import org.rootservices.otter.router.entity.between.RestBetween;

import java.util.List;

public class RestBetweens<U, P> {
    private List<RestBetween<U, P>> before;
    private List<RestBetween<U, P>> after;

    public RestBetweens(List<RestBetween<U, P>> before, List<RestBetween<U, P>> after) {
        this.before = before;
        this.after = after;
    }

    public List<RestBetween<U, P>> getBefore() {
        return before;
    }

    public void setBefore(List<RestBetween<U, P>> before) {
        this.before = before;
    }

    public List<RestBetween<U, P>> getAfter() {
        return after;
    }

    public void setAfter(List<RestBetween<U, P>> after) {
        this.after = after;
    }
}

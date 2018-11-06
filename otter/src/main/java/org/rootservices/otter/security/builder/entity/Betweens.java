package org.rootservices.otter.security.builder.entity;

import org.rootservices.otter.router.entity.Between;

import java.util.List;

public class Betweens<S, U, P> {
    private List<Between<S,U,P>> before;
    private List<Between<S,U,P>> after;

    public Betweens(List<Between<S, U, P>> before, List<Between<S, U, P>> after) {
        this.before = before;
        this.after = after;
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

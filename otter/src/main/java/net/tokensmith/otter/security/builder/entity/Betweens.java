package net.tokensmith.otter.security.builder.entity;

import net.tokensmith.otter.router.entity.between.Between;

import java.util.List;

public class Betweens<S, U> {
    private List<Between<S,U>> before;
    private List<Between<S,U>> after;

    public Betweens(List<Between<S, U>> before, List<Between<S, U>> after) {
        this.before = before;
        this.after = after;
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

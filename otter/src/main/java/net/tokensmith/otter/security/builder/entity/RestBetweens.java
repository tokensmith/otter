package net.tokensmith.otter.security.builder.entity;

import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.router.entity.between.RestBetween;

import java.util.List;

public class RestBetweens<S extends DefaultSession, U> {
    private List<RestBetween<S, U>> before;
    private List<RestBetween<S, U>> after;

    public RestBetweens(List<RestBetween<S, U>> before, List<RestBetween<S, U>> after) {
        this.before = before;
        this.after = after;
    }

    public List<RestBetween<S, U>> getBefore() {
        return before;
    }

    public void setBefore(List<RestBetween<S, U>> before) {
        this.before = before;
    }

    public List<RestBetween<S, U>> getAfter() {
        return after;
    }

    public void setAfter(List<RestBetween<S, U>> after) {
        this.after = after;
    }
}

package net.tokensmith.otter.security.builder.entity;

import net.tokensmith.otter.router.entity.between.RestBetween;

import java.util.List;

public class RestBetweens<U> {
    private List<RestBetween<U>> before;
    private List<RestBetween<U>> after;

    public RestBetweens(List<RestBetween<U>> before, List<RestBetween<U>> after) {
        this.before = before;
        this.after = after;
    }

    public List<RestBetween<U>> getBefore() {
        return before;
    }

    public void setBefore(List<RestBetween<U>> before) {
        this.before = before;
    }

    public List<RestBetween<U>> getAfter() {
        return after;
    }

    public void setAfter(List<RestBetween<U>> after) {
        this.after = after;
    }
}

package org.rootservices.otter.router.entity;

import java.util.regex.Matcher;


public class MatchedRoute {
    private Matcher matcher;
    private Route route;

    public MatchedRoute(Matcher matcher, Route route) {
        this.matcher = matcher;
        this.route = route;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}

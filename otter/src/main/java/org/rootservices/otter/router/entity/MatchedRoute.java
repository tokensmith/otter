package org.rootservices.otter.router.entity;

import org.rootservices.otter.security.session.Session;

import java.util.regex.Matcher;


public class MatchedRoute<S extends Session, U> {
    private Matcher matcher;
    private Route<S, U> route;

    public MatchedRoute(Matcher matcher, Route<S, U> route) {
        this.matcher = matcher;
        this.route = route;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public Route<S, U> getRoute() {
        return route;
    }

    public void setRoute(Route<S, U> route) {
        this.route = route;
    }
}

package org.rootservices.otter.router.entity;

import org.rootservices.otter.security.session.Session;

import java.util.regex.Matcher;


public class MatchedRoute<T extends Session> {
    private Matcher matcher;
    private Route<T> route;

    public MatchedRoute(Matcher matcher, Route<T> route) {
        this.matcher = matcher;
        this.route = route;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public Route<T> getRoute() {
        return route;
    }

    public void setRoute(Route<T> route) {
        this.route = route;
    }
}

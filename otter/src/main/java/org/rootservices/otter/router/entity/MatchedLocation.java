package org.rootservices.otter.router.entity;


import java.util.regex.Matcher;


public class MatchedLocation<S, U> {
    private Matcher matcher;
    private Location<S, U> location;

    public MatchedLocation(Location<S, U> location) {
        this.location = location;
    }

    public MatchedLocation(Matcher matcher, Location<S, U> location) {
        this.matcher = matcher;
        this.location = location;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public Location<S, U> getLocation() {
        return location;
    }

    public void setLocation(Location<S, U> location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(location.getPattern()).toString();
    }
}

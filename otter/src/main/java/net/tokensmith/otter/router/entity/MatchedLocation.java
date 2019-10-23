package net.tokensmith.otter.router.entity;


import java.util.regex.Matcher;


public class MatchedLocation {
    private Matcher matcher;
    private Location location;

    public MatchedLocation(Location location) {
        this.location = location;
    }

    public MatchedLocation(Matcher matcher, Location location) {
        this.matcher = matcher;
        this.location = location;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(location.getPattern()).toString();
    }
}

package org.rootservices.otter.router.entity;


import java.util.regex.Matcher;


public class MatchedCoordinate<S, U> {
    private Matcher matcher;
    private Coordinate<S, U> coordinate;

    public MatchedCoordinate(Coordinate<S, U> coordinate) {
        this.coordinate = coordinate;
    }

    public MatchedCoordinate(Matcher matcher, Coordinate<S, U> coordinate) {
        this.matcher = matcher;
        this.coordinate = coordinate;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public Coordinate<S, U> getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate<S, U> coordinate) {
        this.coordinate = coordinate;
    }
}

package org.rootservices.otter.router;


import org.rootservices.otter.router.entity.Match;
import org.rootservices.otter.router.entity.Route;

import java.util.List;
import java.util.regex.Matcher;

public class Dispatcher {
    private List<Route> routes;

    public Dispatcher(List<Route> routes) {
        this.routes = routes;
    }

    public Match find(String url) {

        for(Route route: routes) {
            Matcher matcher = route.getPattern().matcher(url);
            if (matcher.matches()) {
                Match m = new Match(matcher, route);
                return m;
            }
        }
        return null;
    }
}

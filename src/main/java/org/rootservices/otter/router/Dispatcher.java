package org.rootservices.otter.router;


import org.rootservices.otter.router.entity.Match;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class Dispatcher {
    private List<Route> get = new ArrayList<>();
    private List<Route> post = new ArrayList<>();
    private List<Route> put = new ArrayList<>();
    private List<Route> patch = new ArrayList<>();


    public Optional<Match> find(Method method, String url) {

        for(Route route: routes(method)) {
            Matcher matcher = route.getPattern().matcher(url);
            if (matcher.matches()) {
                Optional<Match> m = Optional.of(new Match(matcher, route));
                return m;
            }
        }
        return Optional.empty();
    }

    protected List<Route> routes(Method method) {
        if (method == Method.GET) {
            return get;
        } else if (method == Method.POST) {
            return post;
        } else if (method == Method.PUT) {
            return put;
        } else if (method == Method.PATCH) {
            return patch;
        }

        return new ArrayList<>();
    }

    public List<Route> getGet() {
        return get;
    }

    public List<Route> getPost() {
        return post;
    }

    public List<Route> getPut() {
        return put;
    }

    public List<Route> getPatch() {
        return patch;
    }
}

package org.rootservices.otter.router;


import org.rootservices.otter.router.entity.MatchedRoute;
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
    private List<Route> delete = new ArrayList<>();
    private List<Route> connect = new ArrayList<>();
    private List<Route> options = new ArrayList<>();
    private List<Route> trace = new ArrayList<>();
    private List<Route> head = new ArrayList<>();


    public Optional<MatchedRoute> find(Method method, String url) {

        for(Route route: routes(method)) {
            Matcher matcher = route.getPattern().matcher(url);
            if (matcher.matches()) {
                Optional<MatchedRoute> m = Optional.of(new MatchedRoute(matcher, route));
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
        } else if (method == Method.DELETE) {
            return delete;
        } else if (method == Method.CONNECT) {
            return connect;
        } else if (method == Method.OPTIONS) {
            return options;
        } else if (method == Method.TRACE) {
            return trace;
        } else if (method == Method.HEAD) {
            return head;
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

    public List<Route> getDelete() {
        return delete;
    }

    public List<Route> getConnect() {
        return connect;
    }

    public List<Route> getOptions() {
        return options;
    }

    public List<Route> getTrace() {
        return trace;
    }

    public List<Route> getHead() {
        return head;
    }
}

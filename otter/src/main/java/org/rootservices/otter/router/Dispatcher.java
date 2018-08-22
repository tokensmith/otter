package org.rootservices.otter.router;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rootservices.otter.router.entity.MatchedRoute;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class Dispatcher<T extends Session> {
    public static final String CONTENT_TYPE_MISMATCH = "content-type does not match. url: {}, request content-type: {}, route content-type: {}";
    protected static Logger LOGGER = LogManager.getLogger(Dispatcher.class);
    private static String OTTER_PREFIX = "/app";
    private static String EMPTY = "";
    private List<Route<T>> get = new ArrayList<>();
    private List<Route<T>> post = new ArrayList<>();
    private List<Route<T>> put = new ArrayList<>();
    private List<Route<T>> patch = new ArrayList<>();
    private List<Route<T>> delete = new ArrayList<>();
    private List<Route<T>> connect = new ArrayList<>();
    private List<Route<T>> options = new ArrayList<>();
    private List<Route<T>> trace = new ArrayList<>();
    private List<Route<T>> head = new ArrayList<>();


    public Optional<MatchedRoute<T>> find(Method method, String url) {
        // this allows urls to resources to not have the otter prefix, /app
        String scrubbedUrl = url.replaceAll(OTTER_PREFIX, EMPTY);

        Optional<MatchedRoute<T>> m = Optional.empty();
        for(Route<T> route: routes(method)) {
            Matcher matcher = route.getPattern().matcher(scrubbedUrl);
            if (matcher.matches()) {
                m = Optional.of(new MatchedRoute<T>(matcher, route));
                break;
            }
        }
        return m;
    }

    protected List<Route<T>> routes(Method method) {
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

    public List<Route<T>> getGet() {
        return get;
    }

    public List<Route<T>> getPost() {
        return post;
    }

    public List<Route<T>> getPut() {
        return put;
    }

    public List<Route<T>> getPatch() {
        return patch;
    }

    public List<Route<T>> getDelete() {
        return delete;
    }

    public List<Route<T>> getConnect() {
        return connect;
    }

    public List<Route<T>> getOptions() {
        return options;
    }

    public List<Route<T>> getTrace() {
        return trace;
    }

    public List<Route<T>> getHead() {
        return head;
    }
}

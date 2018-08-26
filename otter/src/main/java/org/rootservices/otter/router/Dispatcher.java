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

public class Dispatcher<S extends Session, U> {
    public static final String CONTENT_TYPE_MISMATCH = "content-type does not match. url: {}, request content-type: {}, route content-type: {}";
    protected static Logger LOGGER = LogManager.getLogger(Dispatcher.class);
    private static String OTTER_PREFIX = "/app";
    private static String EMPTY = "";
    private List<Route<S, U>> get = new ArrayList<>();
    private List<Route<S, U>> post = new ArrayList<>();
    private List<Route<S, U>> put = new ArrayList<>();
    private List<Route<S, U>> patch = new ArrayList<>();
    private List<Route<S, U>> delete = new ArrayList<>();
    private List<Route<S, U>> connect = new ArrayList<>();
    private List<Route<S, U>> options = new ArrayList<>();
    private List<Route<S, U>> trace = new ArrayList<>();
    private List<Route<S, U>> head = new ArrayList<>();


    public Optional<MatchedRoute<S, U>> find(Method method, String url) {
        // this allows urls to resources to not have the otter prefix, /app
        String scrubbedUrl = url.replaceAll(OTTER_PREFIX, EMPTY);

        Optional<MatchedRoute<S, U>> m = Optional.empty();
        for(Route<S, U> route: routes(method)) {
            Matcher matcher = route.getPattern().matcher(scrubbedUrl);
            if (matcher.matches()) {
                m = Optional.of(new MatchedRoute<S, U>(matcher, route));
                break;
            }
        }
        return m;
    }

    protected List<Route<S, U>> routes(Method method) {
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

    public List<Route<S, U>> getGet() {
        return get;
    }

    public List<Route<S, U>> getPost() {
        return post;
    }

    public List<Route<S, U>> getPut() {
        return put;
    }

    public List<Route<S, U>> getPatch() {
        return patch;
    }

    public List<Route<S, U>> getDelete() {
        return delete;
    }

    public List<Route<S, U>> getConnect() {
        return connect;
    }

    public List<Route<S, U>> getOptions() {
        return options;
    }

    public List<Route<S, U>> getTrace() {
        return trace;
    }

    public List<Route<S, U>> getHead() {
        return head;
    }
}

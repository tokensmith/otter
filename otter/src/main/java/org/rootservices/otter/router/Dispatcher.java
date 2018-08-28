package org.rootservices.otter.router;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rootservices.otter.router.entity.MatchedCoordinate;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Coordinate;
import org.rootservices.otter.security.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class Dispatcher<S extends Session, U> {
    protected static Logger LOGGER = LogManager.getLogger(Dispatcher.class);
    private static String OTTER_PREFIX = "/app";
    private static String EMPTY = "";
    private List<Coordinate<S, U>> get = new ArrayList<>();
    private List<Coordinate<S, U>> post = new ArrayList<>();
    private List<Coordinate<S, U>> put = new ArrayList<>();
    private List<Coordinate<S, U>> patch = new ArrayList<>();
    private List<Coordinate<S, U>> delete = new ArrayList<>();
    private List<Coordinate<S, U>> connect = new ArrayList<>();
    private List<Coordinate<S, U>> options = new ArrayList<>();
    private List<Coordinate<S, U>> trace = new ArrayList<>();
    private List<Coordinate<S, U>> head = new ArrayList<>();


    public Optional<MatchedCoordinate<S, U>> find(Method method, String url) {
        // this allows urls to resources to not have the otter prefix, /app
        String scrubbedUrl = url.replaceAll(OTTER_PREFIX, EMPTY);

        Optional<MatchedCoordinate<S, U>> m = Optional.empty();
        for(Coordinate<S, U> route: coordinates(method)) {
            Matcher matcher = route.getPattern().matcher(scrubbedUrl);
            if (matcher.matches()) {
                m = Optional.of(new MatchedCoordinate<S, U>(matcher, route));
                break;
            }
        }
        return m;
    }

    public List<Coordinate<S, U>> coordinates(Method method) {
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

    public List<Coordinate<S, U>> getGet() {
        return get;
    }

    public List<Coordinate<S, U>> getPost() {
        return post;
    }

    public List<Coordinate<S, U>> getPut() {
        return put;
    }

    public List<Coordinate<S, U>> getPatch() {
        return patch;
    }

    public List<Coordinate<S, U>> getDelete() {
        return delete;
    }

    public List<Coordinate<S, U>> getConnect() {
        return connect;
    }

    public List<Coordinate<S, U>> getOptions() {
        return options;
    }

    public List<Coordinate<S, U>> getTrace() {
        return trace;
    }

    public List<Coordinate<S, U>> getHead() {
        return head;
    }
}

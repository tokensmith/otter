package org.rootservices.otter.router;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rootservices.otter.router.entity.MatchedLocation;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class Dispatcher<S, U> {
    protected static Logger LOGGER = LogManager.getLogger(Dispatcher.class);
    private static String OTTER_PREFIX = "/app";
    private static String EMPTY = "";
    private List<Location<S, U>> get = new ArrayList<>();
    private List<Location<S, U>> post = new ArrayList<>();
    private List<Location<S, U>> put = new ArrayList<>();
    private List<Location<S, U>> patch = new ArrayList<>();
    private List<Location<S, U>> delete = new ArrayList<>();
    private List<Location<S, U>> connect = new ArrayList<>();
    private List<Location<S, U>> options = new ArrayList<>();
    private List<Location<S, U>> trace = new ArrayList<>();
    private List<Location<S, U>> head = new ArrayList<>();


    public Optional<MatchedLocation<S, U>> find(Method method, String url) {
        // this allows urls to resources to not have the otter prefix, /app
        String scrubbedUrl = url.replaceAll(OTTER_PREFIX, EMPTY);

        Optional<MatchedLocation<S, U>> m = Optional.empty();
        for(Location<S, U> route: locations(method)) {
            Matcher matcher = route.getPattern().matcher(scrubbedUrl);
            if (matcher.matches()) {
                m = Optional.of(new MatchedLocation<S, U>(matcher, route));
                break;
            }
        }
        return m;
    }

    public List<Location<S, U>> locations(Method method) {
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

    public List<Location<S, U>> getGet() {
        return get;
    }

    public List<Location<S, U>> getPost() {
        return post;
    }

    public List<Location<S, U>> getPut() {
        return put;
    }

    public List<Location<S, U>> getPatch() {
        return patch;
    }

    public List<Location<S, U>> getDelete() {
        return delete;
    }

    public List<Location<S, U>> getConnect() {
        return connect;
    }

    public List<Location<S, U>> getOptions() {
        return options;
    }

    public List<Location<S, U>> getTrace() {
        return trace;
    }

    public List<Location<S, U>> getHead() {
        return head;
    }
}

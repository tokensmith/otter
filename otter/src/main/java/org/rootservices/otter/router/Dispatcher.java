package org.rootservices.otter.router;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.rootservices.otter.router.entity.MatchedLocation;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class Dispatcher {
    protected static Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);
    private static String OTTER_PREFIX = "/app";
    private static String EMPTY = "";
    private List<Location> get = new ArrayList<>();
    private List<Location> post = new ArrayList<>();
    private List<Location> put = new ArrayList<>();
    private List<Location> patch = new ArrayList<>();
    private List<Location> delete = new ArrayList<>();
    private List<Location> connect = new ArrayList<>();
    private List<Location> options = new ArrayList<>();
    private List<Location> trace = new ArrayList<>();
    private List<Location> head = new ArrayList<>();


    public Optional<MatchedLocation> find(Method method, String url) {
        // this allows urls to resources to not have the otter prefix, /app
        String scrubbedUrl = url.replaceAll(OTTER_PREFIX, EMPTY);

        Optional<MatchedLocation> m = Optional.empty();
        for(Location location: locations(method)) {
            Matcher matcher = location.getPattern().matcher(scrubbedUrl);
            if (matcher.matches()) {
                m = Optional.of(new MatchedLocation(matcher, location));
                break;
            }
        }
        return m;
    }

    public List<Location> locations(Method method) {
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

    public List<Location> getGet() {
        return get;
    }

    public List<Location> getPost() {
        return post;
    }

    public List<Location> getPut() {
        return put;
    }

    public List<Location> getPatch() {
        return patch;
    }

    public List<Location> getDelete() {
        return delete;
    }

    public List<Location> getConnect() {
        return connect;
    }

    public List<Location> getOptions() {
        return options;
    }

    public List<Location> getTrace() {
        return trace;
    }

    public List<Location> getHead() {
        return head;
    }
}

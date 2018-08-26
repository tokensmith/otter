package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.Session;


import java.util.List;
import java.util.regex.Pattern;

public class RouteBuilder<S extends Session, U> {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    private Resource<S, U> resource;
    private List<Between<S, U>> before;
    private List<Between<S, U>> after;

    public RouteBuilder<S, U> path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public RouteBuilder<S, U> contentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public RouteBuilder<S, U> resource(Resource<S, U> resource) {
        this.resource = resource;
        return this;
    }

    public RouteBuilder<S, U> before(List<Between<S, U>> before) {
        this.before = before;
        return this;
    }

    public RouteBuilder<S, U> after(List<Between<S, U>> after) {
        this.after = after;
        return this;
    }

    public Route<S, U> build() {
        return new Route<S, U>(pattern, contentTypes, resource, before, after);
    }
}

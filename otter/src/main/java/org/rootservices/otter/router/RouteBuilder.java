package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.Session;


import java.util.List;
import java.util.regex.Pattern;

public class RouteBuilder<T extends Session> {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    private Resource<T> resource;
    private List<Between<T>> before;
    private List<Between<T>> after;

    public RouteBuilder<T> path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public RouteBuilder<T> contentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public RouteBuilder<T> resource(Resource<T> resource) {
        this.resource = resource;
        return this;
    }

    public RouteBuilder<T> before(List<Between<T>> before) {
        this.before = before;
        return this;
    }

    public RouteBuilder<T> after(List<Between<T>> after) {
        this.after = after;
        return this;
    }

    public Route<T> build() {
        return new Route<T>(pattern, contentTypes, resource, before, after);
    }
}

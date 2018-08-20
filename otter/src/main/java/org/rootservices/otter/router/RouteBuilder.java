package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.Session;


import java.util.List;
import java.util.regex.Pattern;

public class RouteBuilder {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    private Resource resource;
    private List<Between<Session>> before;
    private List<Between<Session>> after;

    public RouteBuilder path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public RouteBuilder contentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public RouteBuilder resource(Resource resource) {
        this.resource = resource;
        return this;
    }

    public RouteBuilder before(List<Between<Session>> before) {
        this.before = before;
        return this;
    }

    public RouteBuilder after(List<Between<Session>> after) {
        this.after = after;
        return this;
    }

    public Route build() {
        return new Route(pattern, contentTypes, resource, before, after);
    }
}

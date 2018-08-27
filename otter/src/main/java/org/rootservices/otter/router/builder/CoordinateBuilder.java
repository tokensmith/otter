package org.rootservices.otter.router.builder;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Coordinate;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.Session;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CoordinateBuilder<S extends Session, U> {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    private Resource<S, U> resource;
    private List<Between<S, U>> before;
    private List<Between<S, U>> after;

    public CoordinateBuilder<S, U> path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public CoordinateBuilder<S, U> contentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public CoordinateBuilder<S, U> resource(Resource<S, U> resource) {
        this.resource = resource;
        return this;
    }

    public CoordinateBuilder<S, U> before(List<Between<S, U>> before) {
        this.before = before;
        return this;
    }

    public CoordinateBuilder<S, U> after(List<Between<S, U>> after) {
        this.after = after;
        return this;
    }

    public Coordinate<S, U> build() {
        Route<S, U> route = new RouteBuilder<S, U>()
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        Map<StatusCode, Route<S, U>> errorRoutes = new HashMap<>();
        return new Coordinate<S, U>(pattern, contentTypes, route, errorRoutes);
    }
}

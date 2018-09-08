package org.rootservices.otter.router.builder;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Route;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LocationBuilder<S, U> {
    private Pattern pattern;
    private List<MimeType> contentTypes = new ArrayList<>();
    private Resource<S, U> resource;
    private List<Between<S, U>> before;
    private List<Between<S, U>> after;
    private Map<StatusCode, Route<S, U>> errorRoutes = new HashMap<>();

    public LocationBuilder<S, U> path(String path) {
        this.pattern = Pattern.compile(path);
        return this;
    }

    public LocationBuilder<S, U> contentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public LocationBuilder<S, U> contentType(MimeType contentType) {
        this.contentTypes.add(contentType);
        return this;
    }

    public LocationBuilder<S, U> resource(Resource<S, U> resource) {
        this.resource = resource;
        return this;
    }

    public LocationBuilder<S, U> before(List<Between<S, U>> before) {
        this.before = before;
        return this;
    }

    public LocationBuilder<S, U> after(List<Between<S, U>> after) {
        this.after = after;
        return this;
    }

    public LocationBuilder<S, U> errorRoute(StatusCode statusCode, Route<S, U> route) {
        this.errorRoutes.put(statusCode, route);
        return this;
    }

    public LocationBuilder<S, U> errorResource(StatusCode statusCode, Resource<S, U> resource) {
        Route<S, U> errorResource = new RouteBuilder<S, U>()
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        this.errorRoutes.put(statusCode, errorResource);
        return this;
    }

    public Location<S, U> build() {
        Route<S, U> route = new RouteBuilder<S, U>()
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        return new Location<S, U>(pattern, contentTypes, route, errorRoutes);
    }
}

package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.MatchedRoute;
import org.rootservices.otter.router.entity.Method;

import java.util.Optional;

public class Engine {
    private Dispatcher dispatcher;

    public Engine(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Optional<Response> route(Request request) {
        Optional<MatchedRoute> matchedRoute = dispatcher.find(request.getMethod(), request.getPathWithParams());

        Optional<Response> response = Optional.empty();
        if (matchedRoute.isPresent()) {
            request.setMatcher(Optional.of(matchedRoute.get().getMatcher()));
            response = Optional.of(executeResourceMethod(matchedRoute.get().getRoute().getResource(), request));
        }

        return response;
    }

    public Response executeResourceMethod(Resource resource, Request request) {
        Response response = null;
        Method method = request.getMethod();

        if (method == Method.GET) {
            response = resource.get(request);
        } else if (method == Method.POST) {
            response = resource.post(request);
        } else if (method == Method.PUT) {
            response = resource.put(request);
        } else if (method == Method.PATCH) {
            response = resource.patch(request);
        } else if (method == Method.DELETE) {
            response = resource.delete(request);
        } else if (method == Method.CONNECT) {
            response = resource.connect(request);
        } else if (method == Method.OPTIONS) {
            response = resource.options(request);
        } else if (method == Method.TRACE) {
            response = resource.trace(request);
        } else if (method == Method.HEAD) {
            response = resource.head(request);
        }

        return response;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}

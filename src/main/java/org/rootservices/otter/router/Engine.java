package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Match;
import org.rootservices.otter.router.entity.Method;

import java.util.Optional;

public class Engine {
    private Dispatcher dispatcher;

    public Engine(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Optional<Response> route(Request request) {
        Optional<Match> match = dispatcher.find(request.getMethod(), request.getPath());

        Optional<Response> response = Optional.empty();
        if (match.isPresent()) {
            request.setMatcher(Optional.of(match.get().getMatcher()));
            response = executeResourceMethod(match.get(), request);
        }

        return response;
    }

    protected Optional<Response> executeResourceMethod(Match match, Request request) {
        Optional<Response> response = Optional.empty();
        
        Method method = request.getMethod();
        Resource resource = match.getRoute().getResource();
        
        if (method == Method.GET) {
             response = Optional.of(resource.get(request));
        } else if (method == Method.POST) {
            response = Optional.of(resource.post(request));
        } else if (method == Method.PUT) {
            response = Optional.of(resource.put(request));
        } else if (method == Method.PATCH) {
            response = Optional.of(resource.patch(request));
        } else if (method == Method.DELETE) {
            response = Optional.of(resource.delete(request));
        } else if (method == Method.CONNECT) {
            response = Optional.of(resource.connect(request));
        } else if (method == Method.OPTIONS) {
            response = Optional.of(resource.options(request));
        } else if (method == Method.TRACE) {
            response = Optional.of(resource.trace(request));
        } else if (method == Method.HEAD) {
            response = Optional.of(resource.head(request));
        }

        return response;
    }
}

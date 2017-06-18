package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.MatchedRoute;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;

import java.util.List;
import java.util.Optional;

public class Engine {
    private Dispatcher dispatcher;

    public Engine(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Optional<Response> route(Request request, Response response) {
        Optional<MatchedRoute> matchedRoute = dispatcher.find(request.getMethod(), request.getPathWithParams());

        Optional<Response> resourceResponse = Optional.empty();
        if (matchedRoute.isPresent()) {
            request.setMatcher(Optional.of(matchedRoute.get().getMatcher()));
            resourceResponse = Optional.of(executeResourceMethod(matchedRoute.get().getRoute(), request, response));
        }

        return resourceResponse;
    }

    public Response executeResourceMethod(Route route, Request request, Response response) {
        Resource resource = route.getResource();
        Response resourceResponse = null;
        Method method = request.getMethod();

        Boolean beforeOK = executeBetween(route.getBefore(), method, request, response);
        if (beforeOK) {
            if (method == Method.GET) {
                resourceResponse = resource.get(request, response);
            } else if (method == Method.POST) {
                resourceResponse = resource.post(request, response);
            } else if (method == Method.PUT) {
                resourceResponse = resource.put(request, response);
            } else if (method == Method.PATCH) {
                resourceResponse = resource.patch(request, response);
            } else if (method == Method.DELETE) {
                resourceResponse = resource.delete(request, response);
            } else if (method == Method.CONNECT) {
                resourceResponse = resource.connect(request, response);
            } else if (method == Method.OPTIONS) {
                resourceResponse = resource.options(request, response);
            } else if (method == Method.TRACE) {
                resourceResponse = resource.trace(request, response);
            } else if (method == Method.HEAD) {
                resourceResponse = resource.head(request, response);
            }
        }
        executeBetween(route.getAfter(), method, request, response);

        return resourceResponse;
    }

    protected Boolean executeBetween(List<Between> betweens, Method method, Request request, Response response) {
        for(Between between: betweens) {
            if(!between.process(method, request, response)){
                return false;
            }
        }
        return true;
    }
    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}

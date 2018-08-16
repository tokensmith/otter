package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.MatchedRoute;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.exception.HaltException;

import java.util.List;
import java.util.Optional;

public class Engine {
    private Dispatcher dispatcher;

    public Engine(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Optional<Response> route(Request request, Response response) throws HaltException {
        Optional<MatchedRoute> matchedRoute = dispatcher.find(
                request.getMethod(), request.getPathWithParams(), request.getContentType()
        );

        Optional<Response> resourceResponse = Optional.empty();
        if (matchedRoute.isPresent()) {
            request.setMatcher(Optional.of(matchedRoute.get().getMatcher()));
            Response r;

            try {
                r = executeResourceMethod(matchedRoute.get().getRoute(), request, response);
            } catch (HaltException e) {
                throw e;
            }

            resourceResponse = Optional.of(r);
        }

        return resourceResponse;
    }

    public Response executeResourceMethod(Route route, Request request, Response response) throws HaltException {
        Resource resource = route.getResource();
        Response resourceResponse = null;
        Method method = request.getMethod();

        try {
            executeBetween(route.getBefore(), method, request, response);
        } catch (HaltException e) {
            throw e;
        }

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

        try {
            executeBetween(route.getAfter(), method, request, response);
        } catch (HaltException e) {
            throw e;
        }

        return resourceResponse;
    }

    protected void executeBetween(List<Between> betweens, Method method, Request request, Response response) throws HaltException {
        for(Between between: betweens) {
            try {
                between.process(method, request, response);
            } catch(HaltException e) {
                throw e;
            }
        }
    }
    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}

package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.router.entity.*;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.router.factory.ErrorRouteFactory;
import org.rootservices.otter.router.factory.ErrorRouteRunnerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Engine<S, U> {
    private Dispatcher<S, U> dispatcher;
    private ErrorRouteFactory<S, U> errorRouteFactory;
    @Deprecated
    private Map<StatusCode, Route<S, U>> errorRoutes = new HashMap<StatusCode, Route<S, U>>();
    private ErrorRouteRunnerFactory errorRouteRunnerFactory;

    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<StatusCode, RouteRunner>();

    public Engine(Dispatcher<S, U> dispatcher, ErrorRouteFactory<S, U> errorRouteFactory, ErrorRouteRunnerFactory errorRouteRunnerFactory) {
        this.dispatcher = dispatcher;
        this.errorRouteFactory = errorRouteFactory;
        this.errorRouteFactory = errorRouteFactory;
    }

    public Response<S> route(Request<S, U> request, Response<S> response) throws HaltException {
        Response<S> resourceResponse;
        Optional<MatchedLocation<S, U>> matchedLocation = dispatcher.find(
                request.getMethod(), request.getPathWithParams()
        );

        try {
            if (matches(matchedLocation, request.getContentType())) {
                request.setMatcher(Optional.of(matchedLocation.get().getMatcher()));
                resourceResponse = executeResourceMethod(matchedLocation.get().getLocation().getRoute(), request, response);
            } else {
                Route<S, U> errorRoute = errorRouteFactory.fromLocation(matchedLocation, errorRoutes);
                resourceResponse = executeResourceMethod(errorRoute, request, response);
            }
        } catch (HaltException e) {
            throw e;
        } catch (Exception e) {
            Route<S, U> serverErrorRoute = errorRouteFactory.serverErrorRoute(matchedLocation, errorRoutes);
            resourceResponse = executeResourceMethod(serverErrorRoute, request, response);
        }

        return resourceResponse;
    }

    protected Boolean matches(Optional<MatchedLocation<S, U>> matchedLocation, MimeType contentType) {
        return matchedLocation.isPresent()
                && ((matchedLocation.get().getLocation().getContentTypes().size() == 0)
                || (matchedLocation.get().getLocation().getContentTypes().size() > 0 && matchedLocation.get().getLocation().getContentTypes().contains(contentType)));
    }

    public Response<S> executeResourceMethod(Route<S, U> route, Request<S, U> request, Response<S> response) throws HaltException {
        Resource<S, U> resource = route.getResource();
        Response<S> resourceResponse = null;
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
            executeBetween(route.getAfter(), method, request, resourceResponse);
        } catch (HaltException e) {
            throw e;
        }

        return resourceResponse;
    }

    protected void executeBetween(List<Between<S, U>> betweens, Method method, Request<S, U> request, Response<S> response) throws HaltException {
        for(Between<S, U> between: betweens) {
            try {
                between.process(method, request, response);
            } catch(HaltException e) {
                throw e;
            }
        }
    }

    public Dispatcher<S, U> getDispatcher() {
        return dispatcher;
    }

    @Deprecated
    public void setErrorRoutes(Map<StatusCode, Route<S, U>> errorRoutes) {
        this.errorRoutes = errorRoutes;
    }

    @Deprecated
    public Map<StatusCode, Route<S, U>> getErrorRoutes() {
        return errorRoutes;
    }
}

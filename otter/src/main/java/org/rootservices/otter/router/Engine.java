package org.rootservices.otter.router;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.*;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.router.exception.MediaTypeException;
import org.rootservices.otter.router.exception.NotFoundException;
import org.rootservices.otter.security.session.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Engine<S extends Session, U> {
    private static String MEDIA_TYPE_MSG = "Unsupported Media Type: %s";
    private static String NOT_FOUND_MSG = "Not Found: %s %s";
    private Dispatcher<S, U> dispatcher;
    protected Map<StatusCode, Route<S, U>> errorRoutes = new HashMap<>();

    public Engine(Dispatcher<S, U> dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Response<S> route(Request<S, U> request, Response<S> response) throws HaltException, MediaTypeException, NotFoundException {
        Response<S> resourceResponse;
        Optional<MatchedCoordinate<S, U>> matchedCoordinate = dispatcher.find(
                request.getMethod(), request.getPathWithParams()
        );

        if (matches(matchedCoordinate, request.getContentType())) {
            request.setMatcher(Optional.of(matchedCoordinate.get().getMatcher()));
            try {
                resourceResponse = executeResourceMethod(matchedCoordinate.get().getCoordinate().getRoute(), request, response);
            } catch (HaltException e) {
                throw e;
            }
        } else if (matchedCoordinate.isPresent()) {
            // should this be a factory for error route?
            throw new MediaTypeException(String.format(MEDIA_TYPE_MSG, request.getContentType()));
        } else {
            throw new NotFoundException(String.format(NOT_FOUND_MSG, request.getMethod(), request.getPathWithParams()));
        }

        return resourceResponse;
    }

    protected Boolean matches(Optional<MatchedCoordinate<S, U>> matchedCoordinate, MimeType contentType) {
        return matchedCoordinate.isPresent()
                && ((matchedCoordinate.get().getCoordinate().getContentTypes().size() == 0)
                || (matchedCoordinate.get().getCoordinate().getContentTypes().size() > 0 && matchedCoordinate.get().getCoordinate().getContentTypes().contains(contentType)));
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

    public void setErrorRoutes(Map<StatusCode, Route<S, U>> errorRoutes) {
        this.errorRoutes = errorRoutes;
    }
}

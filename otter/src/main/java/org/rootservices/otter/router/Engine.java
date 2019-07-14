package org.rootservices.otter.router;


import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.router.entity.*;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.router.factory.ErrorRouteRunnerFactory;

import java.util.*;

public class Engine {
    private Dispatcher dispatcher;
    private ErrorRouteRunnerFactory errorRouteRunnerFactory;
    private Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<StatusCode, RouteRunner>();

    public Engine(Dispatcher dispatcher, ErrorRouteRunnerFactory errorRouteRunnerFactory) {
        this.dispatcher = dispatcher;
        this.errorRouteRunnerFactory = errorRouteRunnerFactory;
    }

    public Answer route(Ask ask, Answer answer) throws HaltException {
        Answer resourceAnswer;
        Optional<MatchedLocation> matchedLocation = dispatcher.find(
                ask.getMethod(), ask.getPathWithParams()
        );

        if (matchedLocation.isPresent()) {
            ask.setMatcher(Optional.of(matchedLocation.get().getMatcher()));
            ask.setPossibleContentTypes(matchedLocation.get().getLocation().getContentTypes());
        } else {
            ask.setMatcher(Optional.empty());
            ask.setPossibleContentTypes(new ArrayList<>());
        }

        try {
            if (matches(matchedLocation, ask.getContentType())) {
                resourceAnswer = matchedLocation.get().getLocation().getRouteRunner().run(ask, answer);
            } else {
                // 113: 404, 414
                RouteRunner errorRouteRunner = errorRouteRunnerFactory.fromLocation(matchedLocation, errorRouteRunners);
                resourceAnswer = errorRouteRunner.run(ask, answer);
            }
        } catch (HaltException e) {
            throw e;
        }
        /*
        TODO: Error Handling: catch, Client Error, Server Error
        should resources be able to throw these or only RouteRunners?
        if resources then the exception could go through many layers.
         */
        catch (Exception e) {
            // TODO: Error Handling: 500
            RouteRunner serverErrorRoute = errorRouteRunnerFactory.serverErrorRouteRunner(matchedLocation, errorRouteRunners);
            resourceAnswer = serverErrorRoute.run(ask, answer);
        }

        return resourceAnswer;
    }

    protected Boolean matches(Optional<MatchedLocation> matchedLocation, MimeType contentType) {
        return matchedLocation.isPresent()
                && ((matchedLocation.get().getLocation().getContentTypes().size() == 0)
                || (matchedLocation.get().getLocation().getContentTypes().size() > 0 && matchedLocation.get().getLocation().getContentTypes().contains(contentType)));
    }

    protected Boolean unsupportedMediaType(Optional<MatchedLocation> matchedLocation, MimeType contentType) {
        return matchedLocation.isPresent()
         && (matchedLocation.get().getLocation().getContentTypes().contains(contentType));
    }


    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setErrorRoutes(Map<StatusCode, RouteRunner> errorRouteRunners) {
        this.errorRouteRunners = errorRouteRunners;
    }

    public Map<StatusCode, RouteRunner> getErrorRoutes() {
        return errorRouteRunners;
    }
}

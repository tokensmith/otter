package net.tokensmith.otter.router;


import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.router.entity.*;
import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.router.entity.io.Ask;
import net.tokensmith.otter.router.exception.HaltException;

import java.util.*;

public class Engine {
    private Dispatcher dispatcher;
    private Dispatcher notFoundDispatcher;


    public Engine(Dispatcher dispatcher, Dispatcher notFoundDispatcher) {
        this.dispatcher = dispatcher;
        this.notFoundDispatcher = notFoundDispatcher;
    }

    public Answer route(Ask ask, Answer answer) throws HaltException {
        Answer resourceAnswer = new Answer();
        Optional<MatchedLocation> matchedLocation = dispatcher.find(
                ask.getMethod(), ask.getPathWithParams()
        );

        if (matchedLocation.isPresent()) {
            ask.setMatcher(Optional.of(matchedLocation.get().getMatcher()));
            ask.setPossibleContentTypes(matchedLocation.get().getLocation().getContentTypes());
            ask.setPossibleAccepts(matchedLocation.get().getLocation().getAccepts());
        } else {
            ask.setMatcher(Optional.empty());
            ask.setPossibleContentTypes(new ArrayList<>());
            ask.setPossibleAccepts(new ArrayList<>());
        }

        try {
            StatusCode matches = to(matchedLocation, ask);
            switch (matches){
                case OK:
                    resourceAnswer = found(matchedLocation.get(), ask, answer);
                    break;
                case NOT_FOUND:
                    resourceAnswer = notFound(ask, answer);
                    break;
                case UNSUPPORTED_MEDIA_TYPE:
                    resourceAnswer = unSupportedMediaType(matchedLocation.get(), ask, answer);
                    break;
                case NOT_ACCEPTABLE:
                    resourceAnswer = notAcceptable(matchedLocation.get(), ask, answer);
                    break;
            }

        } catch (HaltException e) {
            throw e;
        }

        return resourceAnswer;
    }

    protected StatusCode to(Optional<MatchedLocation> matchedLocation, Ask ask) {
        StatusCode to = StatusCode.OK;

        if (matchedLocation.isEmpty()) {
            to = StatusCode.NOT_FOUND;
        } else {
            Location location = matchedLocation.get().getLocation();
            if (location.getContentTypes().size() > 0 && !location.getContentTypes().contains(ask.getContentType())) {
                to = StatusCode.UNSUPPORTED_MEDIA_TYPE;
            } else if (location.getAccepts().size() > 0 && !location.getAccepts().contains(ask.getAccept())) {
                to =  StatusCode.NOT_ACCEPTABLE;
            }
        }
        return to;
    }

    protected Answer found(MatchedLocation foundLocation, Ask ask, Answer answer) throws HaltException {
        ask.setMatcher(Optional.of(foundLocation.getMatcher()));
        ask.setPossibleContentTypes(foundLocation.getLocation().getContentTypes());
        return foundLocation.getLocation().getRouteRunner().run(ask, answer);
    }

    /**
     * Finds the location from the notFoundDispatcher, executes its route, then returns the answer.
     * This does not validate that the ask's content type matches the not found's content-type
     *
     * @param ask Ask to pass to the route runner
     * @param answer Answer to pass to the route runner
     * @return An Answer from the route runner
     * @throws HaltException Could be thrown from the route runner.
     */
    protected Answer notFound(Ask ask, Answer answer) throws HaltException {
        Optional<MatchedLocation> matchedLocation = notFoundDispatcher.find(ask.getMethod(), ask.getPathWithParams());
        MatchedLocation foundLocation = matchedLocation.get();
        ask.setMatcher(Optional.of(foundLocation.getMatcher()));
        ask.setPossibleContentTypes(foundLocation.getLocation().getContentTypes());
        ask.setPossibleAccepts(foundLocation.getLocation().getAccepts());
        return matchedLocation.get().getLocation().getRouteRunner().run(ask, answer);
    }

    protected Answer unSupportedMediaType(MatchedLocation foundLocation, Ask ask, Answer answer) throws HaltException{
        ask.setMatcher(Optional.empty());
        ask.setPossibleContentTypes(foundLocation.getLocation().getContentTypes());
        ask.setPossibleAccepts(foundLocation.getLocation().getAccepts());

        RouteRunner mediaTypeRunner = foundLocation.getLocation().getErrorRouteRunners().get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        return mediaTypeRunner.run(ask, answer);
    }

    protected Answer notAcceptable(MatchedLocation foundLocation, Ask ask, Answer answer) throws HaltException{
        ask.setMatcher(Optional.empty());
        ask.setPossibleContentTypes(foundLocation.getLocation().getContentTypes());
        ask.setPossibleAccepts(foundLocation.getLocation().getAccepts());

        RouteRunner notAcceptable = foundLocation.getLocation().getErrorRouteRunners().get(StatusCode.NOT_ACCEPTABLE);
        return notAcceptable.run(ask, answer);
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public Dispatcher getNotFoundDispatcher() {
        return notFoundDispatcher;
    }
}

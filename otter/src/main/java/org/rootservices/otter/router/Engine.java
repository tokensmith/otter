package org.rootservices.otter.router;


import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.router.entity.*;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;

import java.util.*;

public class Engine {
    private Dispatcher dispatcher;
    private Dispatcher notFoundDispatcher;


    public Engine(Dispatcher dispatcher, Dispatcher notFoundDispatcher) {
        this.dispatcher = dispatcher;
        this.notFoundDispatcher = notFoundDispatcher;
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
                resourceAnswer = found(matchedLocation.get(), ask, answer);
            } else if (matchedLocation.isEmpty()) {
                // not found
                resourceAnswer = notFound(ask, answer);
            } else {
                // unsupported media type.
                resourceAnswer = unSupportedMediaType(matchedLocation.get(), ask, answer);
            }
        } catch (HaltException e) {
            throw e;
        }

        return resourceAnswer;
    }

    protected Boolean matches(Optional<MatchedLocation> matchedLocation, MimeType contentType) {
        return matchedLocation.isPresent()
                && ((matchedLocation.get().getLocation().getContentTypes().size() == 0)
                || (matchedLocation.get().getLocation().getContentTypes().size() > 0 && matchedLocation.get().getLocation().getContentTypes().contains(contentType)));
    }

    protected Boolean unsupportedMediaType(Optional<MatchedLocation> matchedLocation, MimeType contentType) {

        if (matchedLocation.isEmpty())
            return true;
        else if (matchedLocation.get().getLocation().getContentTypes().size() < 1) {
            return true;
        } else if (!matchedLocation.get().getLocation().getContentTypes().contains(contentType)) {
            return true;
        }
        return false;
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
        return matchedLocation.get().getLocation().getRouteRunner().run(ask, answer);
    }

    protected Answer unSupportedMediaType(MatchedLocation foundLocation, Ask ask, Answer answer) throws HaltException{
        ask.setMatcher(Optional.empty());
        ask.setPossibleContentTypes(foundLocation.getLocation().getContentTypes());

        RouteRunner mediaTypeRunner = foundLocation.getLocation().getErrorRouteRunners().get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        return mediaTypeRunner.run(ask, answer);
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public Dispatcher getNotFoundDispatcher() {
        return notFoundDispatcher;
    }
}

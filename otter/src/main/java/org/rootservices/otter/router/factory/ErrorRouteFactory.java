package org.rootservices.otter.router.factory;

import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.MatchedLocation;
import org.rootservices.otter.router.entity.Route;

import java.util.Map;
import java.util.Optional;

public class ErrorRouteFactory<S, U> {

    public Route<S, U> fromLocation(Optional<MatchedLocation<S, U>> matchedLocation, Map<StatusCode, Route<S, U>> errorRoutes) {
        if (matchedLocation.isPresent() && matchedLocation.get().getLocation().getErrorRoutes().get(StatusCode.UNSUPPORTED_MEDIA_TYPE) != null) {
            return matchedLocation.get().getLocation().getErrorRoutes().get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        } else if (matchedLocation.isPresent()) {
            return errorRoutes.get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        } else {
            return errorRoutes.get(StatusCode.NOT_FOUND);
        }
    }

    public Route<S, U> serverErrorRoute(Optional<MatchedLocation<S, U>> matchedLocation, Map<StatusCode, Route<S, U>> errorRoutes) {
        if (matchedLocation.isPresent() && matchedLocation.get().getLocation().getErrorRoutes().get(StatusCode.SERVER_ERROR) != null) {
            return matchedLocation.get().getLocation().getErrorRoutes().get(StatusCode.SERVER_ERROR);
        } else {
            return errorRoutes.get(StatusCode.SERVER_ERROR);
        }
    }
}

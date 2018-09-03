package org.rootservices.otter.router.factory;

import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.MatchedCoordinate;
import org.rootservices.otter.router.entity.Route;

import java.util.Map;
import java.util.Optional;

public class ErrorRouteFactory<S, U> {

    public Route<S, U> fromCoordinate(Optional<MatchedCoordinate<S, U>> matchedCoordinate, Map<StatusCode, Route<S, U>> errorRoutes) {
        if (matchedCoordinate.isPresent() && matchedCoordinate.get().getCoordinate().getErrorRoutes().get(StatusCode.UNSUPPORTED_MEDIA_TYPE) != null) {
            return matchedCoordinate.get().getCoordinate().getErrorRoutes().get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        } else if (matchedCoordinate.isPresent()) {
            return errorRoutes.get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        } else {
            return errorRoutes.get(StatusCode.NOT_FOUND);
        }
    }

    public Route<S, U> serverErrorRoute(Optional<MatchedCoordinate<S, U>> matchedCoordinate, Map<StatusCode, Route<S, U>> errorRoutes) {
        if (matchedCoordinate.isPresent() && matchedCoordinate.get().getCoordinate().getErrorRoutes().get(StatusCode.SERVER_ERROR) != null) {
            return matchedCoordinate.get().getCoordinate().getErrorRoutes().get(StatusCode.SERVER_ERROR);
        } else {
            return errorRoutes.get(StatusCode.SERVER_ERROR);
        }
    }
}

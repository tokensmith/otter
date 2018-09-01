package org.rootservices.otter.router.factory;

import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.MatchedCoordinate;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.Session;

import java.util.Map;
import java.util.Optional;

public class ErrorRouteFactory<S extends Session, U> {

    public Route<S, U> fromCoordiante(Optional<MatchedCoordinate<S, U>> matchedCoordinate, Map<StatusCode, Route<S, U>> errorRoutes) {
        if (matchedCoordinate.isPresent() && matchedCoordinate.get().getCoordinate().getErrorRoutes().get(StatusCode.UNSUPPORTED_MEDIA_TYPE) != null) {
            return matchedCoordinate.get().getCoordinate().getErrorRoutes().get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        } else if (matchedCoordinate.isPresent()) {
            return errorRoutes.get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        } else {
            return errorRoutes.get(StatusCode.NOT_FOUND);
        }
    }
}

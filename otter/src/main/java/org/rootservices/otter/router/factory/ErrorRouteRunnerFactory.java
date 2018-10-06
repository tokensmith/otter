package org.rootservices.otter.router.factory;

import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.router.entity.MatchedLocation;

import java.util.Map;
import java.util.Optional;

public class ErrorRouteRunnerFactory {

    public RouteRunner fromLocation(Optional<MatchedLocation> matchedLocation, Map<StatusCode, RouteRunner> errorRouteRunners) {
        if (matchedLocation.isPresent() && matchedLocation.get().getLocation().getErrorRouteRunners().get(StatusCode.UNSUPPORTED_MEDIA_TYPE) != null) {
            // TODO: 99 remove cast when matchedLocation is no longer parameterized.
            return (RouteRunner) matchedLocation.get().getLocation().getErrorRouteRunners().get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        } else if (matchedLocation.isPresent()) {
            return errorRouteRunners.get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        } else {
            return errorRouteRunners.get(StatusCode.NOT_FOUND);
        }
    }

    public RouteRunner serverErrorRouteRunner(Optional<MatchedLocation> matchedLocation, Map<StatusCode, RouteRunner> errorRouteRunner) {
        if (matchedLocation.isPresent() && matchedLocation.get().getLocation().getErrorRouteRunners().get(StatusCode.SERVER_ERROR) != null) {
            // TODO: 99 remove cast when matchedLocation is no longer parameterized.
            return (RouteRunner) matchedLocation.get().getLocation().getErrorRouteRunners().get(StatusCode.SERVER_ERROR);
        } else {
            return errorRouteRunner.get(StatusCode.SERVER_ERROR);
        }
    }
}

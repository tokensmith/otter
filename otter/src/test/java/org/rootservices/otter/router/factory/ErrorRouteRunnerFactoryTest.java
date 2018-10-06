package org.rootservices.otter.router.factory;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.MatchedLocation;


import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ErrorRouteRunnerFactoryTest {
    private ErrorRouteRunnerFactory subject;

    @Before
    public void setUp() {
        subject = new ErrorRouteRunnerFactory();
    }

    @Test
    public void fromLocationShouldBeLocationUnSupportedErrorRoute() {
        Location location = FixtureFactory.makeLocationWithErrorRoutes("foo");

        Optional<MatchedLocation> match = Optional.of(
                new MatchedLocation(location)
        );

        Map<StatusCode, RouteRunner> errorRouteRunners = FixtureFactory.makeErrorRouteRunners();

        RouteRunner actual = subject.fromLocation(match, errorRouteRunners);

        assertThat(actual, is(location.getErrorRouteRunners().get(StatusCode.UNSUPPORTED_MEDIA_TYPE)));

    }

    @Test
    public void fromLocationShouldBeGlobalUnSupportedErrorRoute() {
        Location location = FixtureFactory.makeLocation("foo");

        Optional<MatchedLocation> match = Optional.of(
                new MatchedLocation(location)
        );

        Map<StatusCode, RouteRunner> errorRouteRunners = FixtureFactory.makeErrorRouteRunners();

        RouteRunner actual = subject.fromLocation(match, errorRouteRunners);

        assertThat(actual, is(errorRouteRunners.get(StatusCode.UNSUPPORTED_MEDIA_TYPE)));

    }

    @Test
    public void fromLocationShouldBeGlobalNotFoundErrorRoute() {
        Optional<MatchedLocation> match = Optional.empty();
        Map<StatusCode, RouteRunner> errorRouteRunners = FixtureFactory.makeErrorRouteRunners();

        RouteRunner actual = subject.fromLocation(match, errorRouteRunners);

        assertThat(actual, is(errorRouteRunners.get(StatusCode.NOT_FOUND)));
    }

    @Test
    public void serverErrorShouldBeLocationErrorRoute() {
        Location location = FixtureFactory.makeLocationWithErrorRoutes("foo");

        Optional<MatchedLocation> match = Optional.of(
                new MatchedLocation(location)
        );

        Map<StatusCode, RouteRunner> errorRouteRunners = FixtureFactory.makeErrorRouteRunners();

        RouteRunner actual = subject.serverErrorRouteRunner(match, errorRouteRunners);

        assertThat(actual, is(location.getErrorRouteRunners().get(StatusCode.SERVER_ERROR)));
    }

    @Test
    public void serverErrorWhenNoLocationErrorRouteShouldBeGlobalRoute() {
        Location location = FixtureFactory.makeLocation("foo");

        Optional<MatchedLocation> match = Optional.of(
                new MatchedLocation(location)
        );

        Map<StatusCode, RouteRunner> errorRouteRunners = FixtureFactory.makeErrorRouteRunners();

        RouteRunner actual = subject.serverErrorRouteRunner(match, errorRouteRunners);

        assertThat(actual, is(errorRouteRunners.get(StatusCode.SERVER_ERROR)));
    }


    @Test
    public void serverErrorShouldBeGlobalRoute() {
        Optional<MatchedLocation> match = Optional.empty();
        Map<StatusCode, RouteRunner> errorRouteRunners = FixtureFactory.makeErrorRouteRunners();

        RouteRunner actual = subject.serverErrorRouteRunner(match, errorRouteRunners);

        assertThat(actual, is(errorRouteRunners.get(StatusCode.SERVER_ERROR)));
    }

}
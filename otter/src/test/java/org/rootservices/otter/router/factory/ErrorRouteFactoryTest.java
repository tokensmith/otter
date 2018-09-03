package org.rootservices.otter.router.factory;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.MatchedLocation;
import org.rootservices.otter.router.entity.Route;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ErrorRouteFactoryTest {
    private ErrorRouteFactory<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        subject = new ErrorRouteFactory<>();
    }

    @Test
    public void fromLocationShouldBeLocationUnSupportedErrorRoute() {
        Location<DummySession, DummyUser> location = FixtureFactory.makeLocationWithErrorRoutes("foo");

        Optional<MatchedLocation<DummySession, DummyUser>> match = Optional.of(
                new MatchedLocation<DummySession, DummyUser>(location)
        );

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Route<DummySession, DummyUser> actual = subject.fromLocation(match, errorRoutes);

        assertThat(actual, is(location.getErrorRoutes().get(StatusCode.UNSUPPORTED_MEDIA_TYPE)));

    }

    @Test
    public void fromLocationShouldBeGlobalUnSupportedErrorRoute() {
        Location<DummySession, DummyUser> location = FixtureFactory.makeLocation("foo");

        Optional<MatchedLocation<DummySession, DummyUser>> match = Optional.of(
                new MatchedLocation<DummySession, DummyUser>(location)
        );

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Route<DummySession, DummyUser> actual = subject.fromLocation(match, errorRoutes);

        assertThat(actual, is(errorRoutes.get(StatusCode.UNSUPPORTED_MEDIA_TYPE)));

    }

    @Test
    public void fromLocationShouldBeGlobalNotFoundErrorRoute() {
        Optional<MatchedLocation<DummySession, DummyUser>> match = Optional.empty();
        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Route<DummySession, DummyUser> actual = subject.fromLocation(match, errorRoutes);

        assertThat(actual, is(errorRoutes.get(StatusCode.NOT_FOUND)));
    }

    @Test
    public void serverErrorShouldBeLocationErrorRoute() {
        Location<DummySession, DummyUser> location = FixtureFactory.makeLocationWithErrorRoutes("foo");

        Optional<MatchedLocation<DummySession, DummyUser>> match = Optional.of(
                new MatchedLocation<DummySession, DummyUser>(location)
        );

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Route<DummySession, DummyUser> actual = subject.serverErrorRoute(match, errorRoutes);

        assertThat(actual, is(location.getErrorRoutes().get(StatusCode.SERVER_ERROR)));
    }

    @Test
    public void serverErrorWhenNoLocationErrorRouteShouldBeGlobalRoute() {
        Location<DummySession, DummyUser> location = FixtureFactory.makeLocation("foo");

        Optional<MatchedLocation<DummySession, DummyUser>> match = Optional.of(
                new MatchedLocation<DummySession, DummyUser>(location)
        );

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Route<DummySession, DummyUser> actual = subject.serverErrorRoute(match, errorRoutes);

        assertThat(actual, is(errorRoutes.get(StatusCode.SERVER_ERROR)));
    }


    @Test
    public void serverErrorShouldBeGlobalRoute() {
        Optional<MatchedLocation<DummySession, DummyUser>> match = Optional.empty();
        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Route<DummySession, DummyUser> actual = subject.serverErrorRoute(match, errorRoutes);

        assertThat(actual, is(errorRoutes.get(StatusCode.SERVER_ERROR)));
    }
}
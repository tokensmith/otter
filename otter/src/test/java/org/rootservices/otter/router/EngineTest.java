package org.rootservices.otter.router;

import helper.FixtureFactory;
import helper.entity.DummyPayload;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.MatchedLocation;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.factory.ErrorRouteRunnerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EngineTest {
    @Mock
    private Dispatcher mockDispatcher;
    @Mock
    private ErrorRouteRunnerFactory mockErrorRouteRunnerFactory;
    private Engine subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new Engine(mockDispatcher, mockErrorRouteRunnerFactory);
    }

    public void routeWhenMethodIsXShouldMatch(Method method) throws Exception {
        String url = "foo";
        Optional<MatchedLocation> match = FixtureFactory.makeRestMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(method);
        ask.setPathWithParams(url);
        ask.setContentType(json);

        Answer answer = FixtureFactory.makeAnswer();

        RouteRunner mockRouteRunner = mock(RouteRunner.class);
        when(mockRouteRunner.run(ask, answer)).thenReturn(answer);

        Location location = new LocationBuilder<DummySession, DummyUser, DummyPayload>()
                .contentTypes(new ArrayList<MimeType>())
                .build();

        // TODO: 99: should this be in the builder?
        location.setRouteRunner(mockRouteRunner);

        match.get().setLocation(location);

        when(mockDispatcher.find(method, url)).thenReturn(match);

        Answer actual = subject.route(ask, answer);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(answer));
    }

    @Test
    public void routeWhenMethodIsGetShouldMatch() throws Exception {
        routeWhenMethodIsXShouldMatch(Method.GET);
    }

    @Test
    public void routeWhenMethodIsPostShouldMatch() throws Exception {
        routeWhenMethodIsXShouldMatch(Method.POST);
    }

    @Test
    public void routeWhenMethodIsPutShouldMatch() throws Exception {
        routeWhenMethodIsXShouldMatch(Method.PUT);
    }

    @Test
    public void routeWhenMethodIsDeleteShouldMatch() throws Exception {
        routeWhenMethodIsXShouldMatch(Method.DELETE);
    }

    @Test
    public void routeWhenMethodIsConnectShouldMatch() throws Exception {
        routeWhenMethodIsXShouldMatch(Method.CONNECT);
    }

    @Test
    public void routeWhenMethodIsOptionsShouldMatch() throws Exception {
        routeWhenMethodIsXShouldMatch(Method.OPTIONS);
    }

    @Test
    public void routeWhenMethodIsTraceShouldMatch() throws Exception {
        routeWhenMethodIsXShouldMatch(Method.TRACE);
    }

    @Test
    public void routeWhenMethodIsHeadShouldMatch() throws Exception {
        routeWhenMethodIsXShouldMatch(Method.HEAD);
    }

    @Test
    public void routeWhenGetAndNoMatchedRouteShouldRunErrorRoute() throws Exception {
        Method method = Method.GET;
        String url = "foo";
        Optional<MatchedLocation> match = Optional.empty();

        MimeType json = new MimeTypeBuilder().json().build();
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(method);
        ask.setPathWithParams(url);
        ask.setContentType(json);

        Answer answer = FixtureFactory.makeAnswer();

        when(mockDispatcher.find(Method.GET, url)).thenReturn(match);

        RouteRunner errorRouteRunner = mock(RouteRunner.class);
        when(errorRouteRunner.run(ask, answer)).thenReturn(answer);

        Map<StatusCode, RouteRunner> errorRoutes = FixtureFactory.makeRestErrorRouteRunners();
        when(mockErrorRouteRunnerFactory.fromLocation(match, errorRoutes)).thenReturn(errorRouteRunner);

        subject.setErrorRoutes(errorRoutes);
        Answer actual = subject.route(ask, answer);

        assertThat(actual, is(answer));
    }
}
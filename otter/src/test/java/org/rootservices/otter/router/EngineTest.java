package org.rootservices.otter.router;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.FakeResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.MatchedLocation;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.factory.ErrorRouteFactory;
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
    private Dispatcher<DummySession, DummyUser> mockDispatcher;
    @Mock
    private ErrorRouteFactory<DummySession, DummyUser> mockErrorRouteFactory;
    @Mock
    private ErrorRouteRunnerFactory mockErrorRouteRunnerFactory;
    private Engine<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new Engine<DummySession, DummyUser>(mockDispatcher, mockErrorRouteFactory, mockErrorRouteRunnerFactory);
    }

    @Test
    public void routeWhenMethodIsGetShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedLocation<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.get(request, response)).thenReturn(response);

        Location<DummySession, DummyUser> location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        match.get().setLocation(location);

        when(mockDispatcher.find(Method.GET, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsPostShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedLocation<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.POST);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.post(request, response)).thenReturn(response);

        Location<DummySession, DummyUser> location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setLocation(location);

        when(mockDispatcher.find(Method.POST, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsPutShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedLocation<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.PUT);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.put(request, response)).thenReturn(response);

        Location<DummySession, DummyUser> location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setLocation(location);
        when(mockDispatcher.find(Method.PUT, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsDeleteShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedLocation<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.DELETE);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.delete(request, response)).thenReturn(response);

        Location<DummySession, DummyUser> location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setLocation(location);
        when(mockDispatcher.find(Method.DELETE, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsConnectShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedLocation<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.CONNECT);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.connect(request, response)).thenReturn(response);

        Location<DummySession, DummyUser> location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setLocation(location);
        when(mockDispatcher.find(Method.CONNECT, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsOptionsShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedLocation<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.OPTIONS);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.options(request, response)).thenReturn(response);

        Location<DummySession, DummyUser> location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setLocation(location);
        when(mockDispatcher.find(Method.OPTIONS, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsTraceShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedLocation<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.TRACE);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.trace(request, response)).thenReturn(response);

        Location<DummySession, DummyUser> location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setLocation(location);
        when(mockDispatcher.find(Method.TRACE, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsHeadShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedLocation<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.HEAD);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.head(request, response)).thenReturn(response);

        Location<DummySession, DummyUser> location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setLocation(location);
        when(mockDispatcher.find(Method.HEAD, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }


    @Test
    public void routeWhenGetAndNoMatchedRouteShouldRunErrorRoute() throws Exception {
        String url = "foo";
        Optional<MatchedLocation<DummySession, DummyUser>> match = Optional.empty();

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        when(mockDispatcher.find(Method.GET, url)).thenReturn(match);

        FakeResource mockErrorResource = mock(FakeResource.class);
        when(mockErrorResource.get(request, response)).thenReturn(response);

        Route<DummySession, DummyUser> errorRoute = new RouteBuilder<DummySession, DummyUser>()
                .resource(mockErrorResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();
        when(mockErrorRouteFactory.fromLocation(match, errorRoutes)).thenReturn(errorRoute);

        subject.setErrorRoutes(errorRoutes);
        Response actual = subject.route(request, response);

        assertThat(actual, is(response));
    }
    
}
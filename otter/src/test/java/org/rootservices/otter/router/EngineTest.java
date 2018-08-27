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
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.builder.CoordinateBuilder;
import org.rootservices.otter.router.entity.Coordinate;
import org.rootservices.otter.router.entity.MatchedCoordinate;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.MediaTypeException;
import org.rootservices.otter.router.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EngineTest {
    @Mock
    private Dispatcher<DummySession, DummyUser> mockDispatcher;
    private Engine<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new Engine<DummySession, DummyUser>(mockDispatcher);
    }

    @Test
    public void routeWhenMethodIsGetShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedCoordinate<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.get(request, response)).thenReturn(response);

        Coordinate<DummySession, DummyUser> coordinate = new CoordinateBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        match.get().setCoordinate(coordinate);

        when(mockDispatcher.find(Method.GET, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsPostShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedCoordinate<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.POST);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.post(request, response)).thenReturn(response);

        Coordinate<DummySession, DummyUser> coordinate = new CoordinateBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setCoordinate(coordinate);

        when(mockDispatcher.find(Method.POST, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsPutShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedCoordinate<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.PUT);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.put(request, response)).thenReturn(response);

        Coordinate<DummySession, DummyUser> coordinate = new CoordinateBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setCoordinate(coordinate);
        when(mockDispatcher.find(Method.PUT, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsDeleteShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedCoordinate<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.DELETE);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.delete(request, response)).thenReturn(response);

        Coordinate<DummySession, DummyUser> coordinate = new CoordinateBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setCoordinate(coordinate);
        when(mockDispatcher.find(Method.DELETE, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsConnectShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedCoordinate<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.CONNECT);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.connect(request, response)).thenReturn(response);

        Coordinate<DummySession, DummyUser> coordinate = new CoordinateBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setCoordinate(coordinate);
        when(mockDispatcher.find(Method.CONNECT, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsOptionsShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedCoordinate<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.OPTIONS);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.options(request, response)).thenReturn(response);

        Coordinate<DummySession, DummyUser> coordinate = new CoordinateBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setCoordinate(coordinate);
        when(mockDispatcher.find(Method.OPTIONS, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsTraceShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedCoordinate<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.TRACE);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.trace(request, response)).thenReturn(response);

        Coordinate<DummySession, DummyUser> coordinate = new CoordinateBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setCoordinate(coordinate);
        when(mockDispatcher.find(Method.TRACE, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }

    @Test
    public void routeWhenMethodIsHeadShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedCoordinate<DummySession, DummyUser>> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.HEAD);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        FakeResource mockResource = mock(FakeResource.class);
        when(mockResource.head(request, response)).thenReturn(response);

        Coordinate<DummySession, DummyUser> coordinate = new CoordinateBuilder<DummySession, DummyUser>()
                .contentTypes(new ArrayList<MimeType>())
                .resource(mockResource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        // set the resource merge the mock one.
        match.get().setCoordinate(coordinate);
        when(mockDispatcher.find(Method.HEAD, url)).thenReturn(match);

        Response<DummySession> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(response));
    }


    @Test
    public void routeWhenGetAndNoMatchedRouteShouldThrowNotFoundException() throws Exception {
        String url = "foo";
        Optional<MatchedCoordinate<DummySession, DummyUser>> match = Optional.empty();

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        when(mockDispatcher.find(Method.GET, url)).thenReturn(match);

        NotFoundException actual = null;
        try {
            subject.route(request, response);
        } catch (NotFoundException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void routeWhenGetAndNoMatchedRouteShouldThrowMediaTypeException() throws Exception {
        String url = "foo";
        Coordinate<DummySession, DummyUser> coordinate = new CoordinateBuilder<DummySession, DummyUser>()
                .contentTypes(Arrays.asList(new MimeTypeBuilder().jwt().build()))
                .build();

        Optional<MatchedCoordinate<DummySession, DummyUser>> match = Optional.of(
                new MatchedCoordinate<DummySession, DummyUser>(coordinate)
        );

        MimeType json = new MimeTypeBuilder().json().build();
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<DummySession> response = FixtureFactory.makeResponse();

        when(mockDispatcher.find(Method.GET, url)).thenReturn(match);

        MediaTypeException actual = null;
        try {
            subject.route(request, response);
        } catch (MediaTypeException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
    }
}
package org.rootservices.otter.router;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.MatchedRoute;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.session.Session;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EngineTest {
    @Mock
    private Dispatcher mockDispatcher;
    private Engine subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new Engine(mockDispatcher);
    }

    @Test
    public void routeWhenMethodIsGetShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedRoute> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.get(request, response)).thenReturn(response);

        // set the resource merge the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispatcher.find(Method.GET, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsPostShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedRoute> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.POST);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.post(request, response)).thenReturn(response);

        // set the resource merge the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispatcher.find(Method.POST, url)).thenReturn(match);

        Optional<Response<Session>> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsPutShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedRoute> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.PUT);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.put(request, response)).thenReturn(response);

        // set the resource merge the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispatcher.find(Method.PUT, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsDeleteShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedRoute> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.DELETE);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.delete(request, response)).thenReturn(response);

        // set the resource merge the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispatcher.find(Method.DELETE, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsConnectShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedRoute> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.CONNECT);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.connect(request, response)).thenReturn(response);

        // set the resource merge the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispatcher.find(Method.CONNECT, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsOptionsShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedRoute> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.OPTIONS);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.options(request, response)).thenReturn(response);

        // set the resource merge the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispatcher.find(Method.OPTIONS, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsTraceShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedRoute> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.TRACE);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.trace(request, response)).thenReturn(response);

        // set the resource merge the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispatcher.find(Method.TRACE, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsHeadShouldMatch() throws Exception {
        String url = "foo";
        Optional<MatchedRoute> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Request<Session> request = FixtureFactory.makeRequestSession();
        request.setMethod(Method.HEAD);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response<Session> response = FixtureFactory.makeResponseSession();

        Resource<Session> mockResource = mock(Resource.class);
        when(mockResource.head(request, response)).thenReturn(response);

        // set the resource merge the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispatcher.find(Method.HEAD, url)).thenReturn(match);

        Optional<Response<Session>> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }


    @Test
    public void routeWhenGetAndNoMatchedRouteShouldBeEmptyResponse() throws Exception {
        String url = "foo";
        Optional<MatchedRoute> match = Optional.empty();

        MimeType json = new MimeTypeBuilder().json().build();
        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);
        request.setPathWithParams(url);
        request.setContentType(json);

        Response response = FixtureFactory.makeResponse();

        when(mockDispatcher.find(Method.GET, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(false));
    }
}
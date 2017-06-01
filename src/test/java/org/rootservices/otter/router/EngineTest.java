package org.rootservices.otter.router;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Match;
import org.rootservices.otter.router.entity.Method;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EngineTest {
    @Mock
    private Dispatcher mockDispacter;
    private Engine subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new Engine(mockDispacter);
    }

    @Test
    public void routeWhenMethodIsGetShouldMatch() {
        String url = "foo";
        Optional<Match> match = FixtureFactory.makeMatch(url);

        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);
        request.setPath(url);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.get(request)).thenReturn(response);

        // set the resource to the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispacter.find(Method.GET, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsPostShouldMatch() {
        String url = "foo";
        Optional<Match> match = FixtureFactory.makeMatch(url);

        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.POST);
        request.setPath(url);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.post(request)).thenReturn(response);

        // set the resource to the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispacter.find(Method.POST, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsPutShouldMatch() {
        String url = "foo";
        Optional<Match> match = FixtureFactory.makeMatch(url);

        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.PUT);
        request.setPath(url);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.put(request)).thenReturn(response);

        // set the resource to the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispacter.find(Method.PUT, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsDeleteShouldMatch() {
        String url = "foo";
        Optional<Match> match = FixtureFactory.makeMatch(url);

        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.DELETE);
        request.setPath(url);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.delete(request)).thenReturn(response);

        // set the resource to the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispacter.find(Method.DELETE, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsConnectShouldMatch() {
        String url = "foo";
        Optional<Match> match = FixtureFactory.makeMatch(url);

        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.CONNECT);
        request.setPath(url);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.connect(request)).thenReturn(response);

        // set the resource to the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispacter.find(Method.CONNECT, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsOptionsShouldMatch() {
        String url = "foo";
        Optional<Match> match = FixtureFactory.makeMatch(url);

        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.OPTIONS);
        request.setPath(url);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.options(request)).thenReturn(response);

        // set the resource to the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispacter.find(Method.OPTIONS, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsTraceShouldMatch() {
        String url = "foo";
        Optional<Match> match = FixtureFactory.makeMatch(url);

        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.TRACE);
        request.setPath(url);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.trace(request)).thenReturn(response);

        // set the resource to the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispacter.find(Method.TRACE, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }

    @Test
    public void routeWhenMethodIsHeadShouldMatch() {
        String url = "foo";
        Optional<Match> match = FixtureFactory.makeMatch(url);

        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.HEAD);
        request.setPath(url);

        Response response = FixtureFactory.makeResponse();

        Resource mockResource = mock(Resource.class);
        when(mockResource.head(request)).thenReturn(response);

        // set the resource to the mock one.
        match.get().getRoute().setResource(mockResource);
        when(mockDispacter.find(Method.HEAD, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(response));
    }


    @Test
    public void routeShouldNotMatch() {
        String url = "foo";
        Optional<Match> match = Optional.empty();

        Request request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);
        request.setPath(url);

        when(mockDispacter.find(Method.GET, url)).thenReturn(match);

        Optional<Response> actual = subject.route(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(false));
    }
}
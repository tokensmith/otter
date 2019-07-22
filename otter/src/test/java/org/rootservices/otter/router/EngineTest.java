package org.rootservices.otter.router;

import helper.FixtureFactory;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class EngineTest {
    @Mock
    private Dispatcher mockDispatcher;
    @Mock
    private Dispatcher mockNotFoundDispatcher;

    private Engine subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new Engine(mockDispatcher, mockNotFoundDispatcher);
    }

    public Ask askForEngineTests(Method method, String url, MimeType mimeType) {
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(method);
        ask.setPathWithParams(url);
        ask.setContentType(mimeType);
        ask.setPossibleContentTypes(new ArrayList<>()); // empty list to make sure it gets assigned.
        return ask;
    }



    public void routeWhenMethodIsXShouldMatch(Method method) throws Exception {
        String url = "foo";
        Optional<MatchedLocation> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Ask ask = askForEngineTests(method, url, json);
        Answer answer = FixtureFactory.makeAnswer();

        RouteRunner mockRouteRunner = mock(RouteRunner.class);
        when(mockRouteRunner.run(ask, answer)).thenReturn(answer);

        List<MimeType> contentTypes = new ArrayList<>();
        contentTypes.add(json);

        Location location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(contentTypes)
                .build();

        location.setRouteRunner(mockRouteRunner);

        match.get().setLocation(location);

        when(mockDispatcher.find(method, url)).thenReturn(match);

        Answer actual = subject.route(ask, answer);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(answer));

        // these should have been assigned.
        assertThat(ask.getPossibleContentTypes().size(), is(1));
        assertThat(ask.getPossibleContentTypes(), is(contentTypes));
        assertThat(ask.getMatcher(), is(notNullValue()));
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


    public Optional<MatchedLocation> matchedLocation(String url, MimeType contentType) {
        Optional<MatchedLocation> match = FixtureFactory.makeMatch(url);

        List<MimeType> contentTypes = new ArrayList<>();
        contentTypes.add(contentType);

        Location location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(contentTypes)
                .build();

        match.get().setLocation(location);
        return match;
    }

    @Test
    public void routeWhenGetAndNoMatchedRouteShouldUseNotFound() throws Exception {
        Method method = Method.GET;
        String url = "foo";

        MimeType json = new MimeTypeBuilder().json().build();
        Ask ask = askForEngineTests(method, url, json);
        Answer answer = FixtureFactory.makeAnswer();

        // dispatcher will return an empty response
        Optional<MatchedLocation> emptyMatch = Optional.empty();
        when(mockDispatcher.find(Method.GET, url)).thenReturn(emptyMatch);

        // notFoundDispatcher will return a matched location.
        Optional<MatchedLocation> notFoundMatchedLocation = matchedLocation(url, json);
        RouteRunner mockRouteRunner = mock(RouteRunner.class);
        Answer notFoundAnswer = FixtureFactory.makeAnswer();
        notFoundAnswer.setStatusCode(StatusCode.NOT_FOUND);
        when(mockRouteRunner.run(ask, answer)).thenReturn(notFoundAnswer);
        notFoundMatchedLocation.get().getLocation().setRouteRunner(mockRouteRunner);
        when(mockNotFoundDispatcher.find(Method.GET, url)).thenReturn(notFoundMatchedLocation);

        Answer actual = subject.route(ask, answer);

        assertThat(actual, is(notFoundAnswer));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_FOUND));

        // from the not found location match.
        assertThat(ask.getPossibleContentTypes().size(), is(1));
        assertThat(ask.getPossibleContentTypes().get(0), is(json));
        assertThat(ask.getMatcher().isPresent(), is(true));
    }

    @Test
    public void unsupportedMediaTypeWhenNotPresentShouldBeTrue() {
        Optional<MatchedLocation> match = Optional.empty();
        MimeType json = new MimeTypeBuilder().json().build();

        Boolean actual = subject.unsupportedMediaType(match, json);

        assertThat(actual, is(true));
    }

    @Test
    public void unsupportedMediaTypeWhenNoContentTypesShouldBeTrue() {
        String url = "foo";
        Optional<MatchedLocation> match = FixtureFactory.makeMatch(url);
        match.get().getLocation().setContentTypes(new ArrayList<>()); // empty them out.
        MimeType json = new MimeTypeBuilder().json().build();

        Boolean actual = subject.unsupportedMediaType(match, json);

        assertThat(actual, is(true));
    }

    @Test
    public void unsupportedMediaTypeWhenContentTypesDoNotMatchShouldBeTrue() {
        String url = "foo";
        Optional<MatchedLocation> match = FixtureFactory.makeMatch(url);

        // location to html.
        List<MimeType> contentTypes = new ArrayList<>();
        MimeType html = new MimeTypeBuilder().html().build();
        contentTypes.add(html);
        match.get().getLocation().setContentTypes(contentTypes);

        // actual is json
        MimeType json = new MimeTypeBuilder().json().build();

        Boolean actual = subject.unsupportedMediaType(match, json);

        assertThat(actual, is(true));
    }

    @Test
    public void unsupportedMediaTypeShouldBeFalse() {
        String url = "foo";
        Optional<MatchedLocation> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();

        // location to json.
        List<MimeType> contentTypes = new ArrayList<>();
        contentTypes.add(json);
        match.get().getLocation().setContentTypes(contentTypes);

        Boolean actual = subject.unsupportedMediaType(match, json);

        assertThat(actual, is(false));
    }
}
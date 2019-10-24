package net.tokensmith.otter.router;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.otter.controller.builder.MimeTypeBuilder;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.router.builder.LocationBuilder;
import net.tokensmith.otter.router.entity.Location;
import net.tokensmith.otter.router.entity.MatchedLocation;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.router.entity.io.Ask;


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

    public Ask askForEngineTests(Method method, String url, MimeType contentType, MimeType accept) {
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(method);
        ask.setPathWithParams(url);
        ask.setContentType(contentType);
        ask.setAccept(accept);
        ask.setPossibleContentTypes(new ArrayList<>()); // empty list to make sure it gets assigned.
        ask.setPossibleAccepts(new ArrayList<>()); // empty list to make sure it gets assigned.
        return ask;
    }



    public void routeWhenMethodIsXShouldMatch(Method method) throws Exception {
        String url = "foo";
        Optional<MatchedLocation> match = FixtureFactory.makeMatch(url);

        MimeType json = new MimeTypeBuilder().json().build();
        Ask ask = askForEngineTests(method, url, json, json);
        Answer answer = FixtureFactory.makeAnswer();

        RouteRunner mockRouteRunner = mock(RouteRunner.class);
        when(mockRouteRunner.run(ask, answer)).thenReturn(answer);

        List<MimeType> contentTypes = new ArrayList<>();
        contentTypes.add(json);

        Location location = new LocationBuilder<DummySession, DummyUser>()
                .contentTypes(contentTypes)
                .accepts(contentTypes)
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
        assertThat(ask.getPossibleAccepts().size(), is(1));
        assertThat(ask.getPossibleAccepts(), is(contentTypes));
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
                .accepts(contentTypes)
                .build();

        match.get().setLocation(location);
        return match;
    }

    @Test
    public void routeWhenGetAndNoMatchedRouteShouldUseNotFound() throws Exception {
        Method method = Method.GET;
        String url = "foo";

        MimeType json = new MimeTypeBuilder().json().build();
        Ask ask = askForEngineTests(method, url, json, json);
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
    public void toShouldBeOK() {
        String url = "foo";
        Optional<MatchedLocation> match = FixtureFactory.makeMatch(url);

        // location to html.
        List<MimeType> contentTypes = new ArrayList<>();
        MimeType form = new MimeTypeBuilder().form().build();
        contentTypes.add(form);
        match.get().getLocation().setContentTypes(contentTypes);

        // accepts
        List<MimeType> accepts = new ArrayList<>();
        MimeType html = new MimeTypeBuilder().html().build();
        accepts.add(html);
        match.get().getLocation().setAccepts(accepts);

        Ask ask = askForEngineTests(Method.GET, url, form, html);

        StatusCode actual = subject.to(match, ask);

        assertThat(actual, is(StatusCode.OK));
    }

    @Test
    public void toShouldBeNotFound() {
        MimeType json = new MimeTypeBuilder().json().build();
        Optional<MatchedLocation> match = Optional.empty();
        Ask ask = askForEngineTests(Method.GET, "foo", json, json);

        StatusCode actual = subject.to(match, ask);

        assertThat(actual, is(StatusCode.NOT_FOUND));
    }

    @Test
    public void toShouldBeUnsupportedMediaType() {
        String url = "foo";
        Optional<MatchedLocation> match = FixtureFactory.makeMatch(url);

        // location to html.
        List<MimeType> contentTypes = new ArrayList<>();
        MimeType html = new MimeTypeBuilder().html().build();
        contentTypes.add(html);
        match.get().getLocation().setContentTypes(contentTypes);

        MimeType form = new MimeTypeBuilder().form().build();
        Ask ask = askForEngineTests(Method.GET, url, form, form);

        StatusCode actual = subject.to(match, ask);

        assertThat(actual, is(StatusCode.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    public void toShouldBeNotAcceptable() {
        String url = "foo";
        Optional<MatchedLocation> match = FixtureFactory.makeMatch(url);

        // location to html.
        List<MimeType> contentTypes = new ArrayList<>();
        MimeType form = new MimeTypeBuilder().form().build();
        contentTypes.add(form);
        match.get().getLocation().setContentTypes(contentTypes);

        // accepts
        List<MimeType> accepts = new ArrayList<>();
        MimeType html = new MimeTypeBuilder().html().build();
        accepts.add(html);
        match.get().getLocation().setAccepts(accepts);

        // wonky but it will make the result to not accepted.
        MimeType json = new MimeTypeBuilder().json().build();
        Ask ask = askForEngineTests(Method.GET, url, form, json);

        StatusCode actual = subject.to(match, ask);

        assertThat(actual, is(StatusCode.NOT_ACCEPTABLE));
    }
}
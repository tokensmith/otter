package org.rootservices.otter.gateway.translator;

import helper.FixtureFactory;
import helper.entity.*;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.RouteRun;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.factory.BetweenFlyweight;
import org.rootservices.otter.security.builder.entity.Betweens;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LocationTranslatorTest {
    private LocationTranslator<DummySession, DummyUser> subject;
    @Mock
    private BetweenFlyweight<DummySession, DummyUser> mockBetweenFlyweight;
    private Map<StatusCode, Resource<DummySession, DummyUser>> errorResources = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new LocationTranslator<DummySession, DummyUser>(mockBetweenFlyweight, errorResources);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toShouldBeOk() {
        Betweens<DummySession, DummyUser> betweens = FixtureFactory.makeBetweens();
        when(mockBetweenFlyweight.make(any(), any())).thenReturn(betweens);

        Target<DummySession, DummyUser> target = FixtureFactory.makeTarget();

        Map<Method, Location> actual =  subject.to(target);

        assertThat(actual.size(), is(2));

        // GET
        assertThat(actual.get(Method.GET).getRouteRunner(), Is.is(notNullValue()));

        assertThat(actual.get(Method.GET).getPattern(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes().size(), Is.is(1));

        assertThat(actual.get(Method.GET).getErrorRouteRunners(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getErrorRouteRunners().size(), Is.is(1));

        RouteRun<DummySession, DummyUser> getRouteRunner = (RouteRun<DummySession, DummyUser>) actual.get(Method.GET).getRouteRunner();

        // ordering of before.
        assertThat(getRouteRunner.getRoute().getBefore().get(0), is(betweens.getBefore().get(0)));
        assertThat(getRouteRunner.getRoute().getBefore().get(1), is(target.getBefore().get(0)));
        assertThat(getRouteRunner.getRoute().getBefore().get(2), is(target.getBefore().get(1)));

        // ordering of after
        assertThat(getRouteRunner.getRoute().getAfter().get(0), is(betweens.getAfter().get(0)));
        assertThat(getRouteRunner.getRoute().getAfter().get(1), is(target.getAfter().get(0)));
        assertThat(getRouteRunner.getRoute().getAfter().get(2), is(target.getAfter().get(1)));

        // POST
        assertThat(actual.get(Method.POST).getRouteRunner(), Is.is(notNullValue()));

        assertThat(actual.get(Method.POST).getPattern(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getContentTypes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getContentTypes().size(), Is.is(1));

        assertThat(actual.get(Method.POST).getErrorRouteRunners(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getErrorRouteRunners().size(), Is.is(1));

        RouteRun<DummySession, DummyUser> postRouteRunner = (RouteRun<DummySession, DummyUser>) actual.get(Method.POST).getRouteRunner();

        // ordering of before.
        assertThat(postRouteRunner.getRoute().getBefore().get(0), is(betweens.getBefore().get(0)));
        assertThat(postRouteRunner.getRoute().getBefore().get(1), is(target.getBefore().get(0)));
        assertThat(postRouteRunner.getRoute().getBefore().get(2), is(target.getBefore().get(1)));

        // ordering of after.
        assertThat(postRouteRunner.getRoute().getAfter().get(0), is(betweens.getAfter().get(0)));
        assertThat(postRouteRunner.getRoute().getAfter().get(1), is(target.getAfter().get(0)));
        assertThat(postRouteRunner.getRoute().getAfter().get(2), is(target.getAfter().get(1)));
    }

    @Test
    public void mergeErrorResourcesPreferRight() {
        Map<StatusCode, Resource<DummySession, DummyUser>> left = new HashMap<>();
        Map<StatusCode, Resource<DummySession, DummyUser>> right = new HashMap<>();

        ClientErrorResource leftClientError = new ClientErrorResource();

        left.put(StatusCode.BAD_REQUEST, leftClientError);

        ClientErrorResource rightClientError = new ClientErrorResource();

        right.put(StatusCode.BAD_REQUEST, rightClientError);

        Map<StatusCode, Resource<DummySession, DummyUser>> actual = subject.mergeErrorResources(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(StatusCode.BAD_REQUEST), is(rightClientError));
    }


    @Test
    public void mergeErrorResourcesMergesNonMatching() {
        Map<StatusCode, Resource<DummySession, DummyUser>> left = new HashMap<>();
        Map<StatusCode, Resource<DummySession, DummyUser>> right = new HashMap<>();

        ClientErrorResource leftClientError = new ClientErrorResource();
        left.put(StatusCode.BAD_REQUEST, leftClientError);

        ServerErrorResource rightServerError = new ServerErrorResource();
        right.put(StatusCode.SERVER_ERROR, rightServerError);

        Map<StatusCode, Resource<DummySession, DummyUser>> actual = subject.mergeErrorResources(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(2));
        assertThat(actual.get(StatusCode.SERVER_ERROR), is(rightServerError));
        assertThat(actual.get(StatusCode.BAD_REQUEST), is(leftClientError));
    }

    @Test
    public void mergeErrorResourcesLeftEmpty() {
        Map<StatusCode, Resource<DummySession, DummyUser>> left = new HashMap<>();
        Map<StatusCode, Resource<DummySession, DummyUser>> right = new HashMap<>();

        ServerErrorResource rightServerError = new ServerErrorResource();
        right.put(StatusCode.SERVER_ERROR, rightServerError);

        Map<StatusCode, Resource<DummySession, DummyUser>> actual = subject.mergeErrorResources(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(StatusCode.SERVER_ERROR), is(rightServerError));
    }

    @Test
    public void mergeErrorResourcesRightEmpty() {
        Map<StatusCode, Resource<DummySession, DummyUser>> left = new HashMap<>();
        Map<StatusCode, Resource<DummySession, DummyUser>> right = new HashMap<>();

        ClientErrorResource leftClientError = new ClientErrorResource();
        left.put(StatusCode.BAD_REQUEST, leftClientError);

        Map<StatusCode, Resource<DummySession, DummyUser>> actual = subject.mergeErrorResources(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(StatusCode.BAD_REQUEST), is(leftClientError));
    }

    @Test
    public void mergeErrorResourcesLeftAndRightEmpty() {
        Map<StatusCode, Resource<DummySession, DummyUser>> left = new HashMap<>();
        Map<StatusCode, Resource<DummySession, DummyUser>> right = new HashMap<>();

        Map<StatusCode, Resource<DummySession, DummyUser>> actual = subject.mergeErrorResources(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }
}
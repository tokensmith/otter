package org.rootservices.otter.gateway.translator;

import helper.FixtureFactory;
import helper.entity.DummyPayload;
import helper.entity.DummyUser;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.dispatch.JsonRouteRun;
import org.rootservices.otter.gateway.entity.RestTarget;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.factory.RestBetweenFlyweight;
import org.rootservices.otter.security.builder.entity.RestBetweens;

import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RestLocationTranslatorTest {
    private RestLocationTranslator<DummyUser, DummyPayload> subject;
    @Mock
    private RestBetweenFlyweight<DummyUser> mockRestBetweenFlyweight;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new RestLocationTranslator<DummyUser, DummyPayload>(mockRestBetweenFlyweight);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toShouldBeOk() {
        RestBetweens<DummyUser> betweens = FixtureFactory.makeRestBetweens();
        when(mockRestBetweenFlyweight.make(any(), any())).thenReturn(betweens);

        RestTarget<DummyUser, DummyPayload> target = FixtureFactory.makeRestTarget();

        Map<Method, Location> actual =  subject.to(target);

        assertThat(actual.size(), is(2));

        // GET
        assertThat(actual.get(Method.GET).getRouteRunner(), Is.is(notNullValue()));

        assertThat(actual.get(Method.GET).getPattern(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes().size(), Is.is(1));

        assertThat(actual.get(Method.GET).getErrorRouteRunners(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getErrorRouteRunners().size(), Is.is(1));

        JsonRouteRun<DummyUser, DummyPayload> getRouteRunner = (JsonRouteRun<DummyUser, DummyPayload>) actual.get(Method.GET).getRouteRunner();

        // ordering of before.
        assertThat(getRouteRunner.getRestRoute().getBefore().get(0), is(betweens.getBefore().get(0)));
        assertThat(getRouteRunner.getRestRoute().getBefore().get(1), is(target.getBefore().get(0)));
        assertThat(getRouteRunner.getRestRoute().getBefore().get(2), is(target.getBefore().get(1)));

        // ordering of after
        assertThat(getRouteRunner.getRestRoute().getAfter().get(0), is(betweens.getAfter().get(0)));
        assertThat(getRouteRunner.getRestRoute().getAfter().get(1), is(target.getAfter().get(0)));
        assertThat(getRouteRunner.getRestRoute().getAfter().get(2), is(target.getAfter().get(1)));

        // POST
        assertThat(actual.get(Method.POST).getRouteRunner(), Is.is(notNullValue()));

        assertThat(actual.get(Method.POST).getPattern(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getContentTypes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getContentTypes().size(), Is.is(1));

        assertThat(actual.get(Method.POST).getErrorRouteRunners(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getErrorRouteRunners().size(), Is.is(1));

        JsonRouteRun<DummyUser, DummyPayload> postRouteRunner = (JsonRouteRun<DummyUser, DummyPayload>) actual.get(Method.POST).getRouteRunner();

        // ordering of before.
        assertThat(postRouteRunner.getRestRoute().getBefore().get(0), is(betweens.getBefore().get(0)));
        assertThat(postRouteRunner.getRestRoute().getBefore().get(1), is(target.getBefore().get(0)));
        assertThat(postRouteRunner.getRestRoute().getBefore().get(2), is(target.getBefore().get(1)));

        // ordering of after.
        assertThat(postRouteRunner.getRestRoute().getAfter().get(0), is(betweens.getAfter().get(0)));
        assertThat(postRouteRunner.getRestRoute().getAfter().get(1), is(target.getAfter().get(0)));
        assertThat(postRouteRunner.getRestRoute().getAfter().get(2), is(target.getAfter().get(1)));
    }

}
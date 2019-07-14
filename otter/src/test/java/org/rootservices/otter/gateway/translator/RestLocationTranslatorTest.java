package org.rootservices.otter.gateway.translator;

import helper.FixtureFactory;
import helper.entity.*;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.JsonRouteRun;
import org.rootservices.otter.gateway.entity.rest.RestError;
import org.rootservices.otter.gateway.entity.rest.RestTarget;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.factory.RestBetweenFlyweight;
import org.rootservices.otter.security.builder.entity.RestBetweens;
import org.rootservices.otter.translatable.Translatable;

import java.util.HashMap;
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
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> restErrors = new HashMap<>();
        restErrors.put(StatusCode.BAD_REQUEST, new RestError<>(DummyErrorPayload.class, new ClientErrorRestResource()));

        // 113: need to add defaults here.
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> defaultErrors = new HashMap<>();
        subject = new RestLocationTranslator<DummyUser, DummyPayload>(mockRestBetweenFlyweight, restErrors, defaultErrors);
    }

    @Test
    public void mergeRestErrorsPreferRight() {
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> left = new HashMap<>();
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> right = new HashMap<>();

        ClientErrorRestResource leftClientError = new ClientErrorRestResource();
        RestError<DummyUser, DummyErrorPayload> leftRestError = new RestError<>(DummyErrorPayload.class, leftClientError);

        left.put(StatusCode.BAD_REQUEST, leftRestError);

        ClientErrorRestResource rightClientError = new ClientErrorRestResource();
        RestError<DummyUser, DummyErrorPayload> rightRestError = new RestError<>(DummyErrorPayload.class, rightClientError);

        right.put(StatusCode.BAD_REQUEST, rightRestError);

        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> actual = subject.mergeRestErrors(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(StatusCode.BAD_REQUEST), is(rightRestError));
    }


    @Test
    public void mergeRestErrorsMergesNonMatching() {
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> left = new HashMap<>();
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> right = new HashMap<>();

        ClientErrorRestResource leftClientError = new ClientErrorRestResource();
        RestError<DummyUser, DummyErrorPayload> leftRestError = new RestError<>(DummyErrorPayload.class, leftClientError);

        left.put(StatusCode.BAD_REQUEST, leftRestError);

        ServerErrorRestResource rightServerError = new ServerErrorRestResource();
        RestError<DummyUser, DummyErrorPayload> rightServerRestError = new RestError<>(DummyErrorPayload.class, rightServerError);

        right.put(StatusCode.SERVER_ERROR, rightServerRestError);

        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> actual = subject.mergeRestErrors(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(2));
        assertThat(actual.get(StatusCode.SERVER_ERROR), is(rightServerRestError));
        assertThat(actual.get(StatusCode.BAD_REQUEST), is(leftRestError));
    }

    @Test
    public void mergeRestErrorsLeftEmpty() {
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> left = new HashMap<>();
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> right = new HashMap<>();

        ServerErrorRestResource rightServerError = new ServerErrorRestResource();
        RestError<DummyUser, DummyErrorPayload> rightServerRestError = new RestError<>(DummyErrorPayload.class, rightServerError);

        right.put(StatusCode.SERVER_ERROR, rightServerRestError);

        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> actual = subject.mergeRestErrors(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(StatusCode.SERVER_ERROR), is(rightServerRestError));
    }

    @Test
    public void mergeRestErrorsRightEmpty() {
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> left = new HashMap<>();
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> right = new HashMap<>();

        ClientErrorRestResource leftClientError = new ClientErrorRestResource();
        RestError<DummyUser, DummyErrorPayload> leftRestError = new RestError<>(DummyErrorPayload.class, leftClientError);

        left.put(StatusCode.BAD_REQUEST, leftRestError);

        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> actual = subject.mergeRestErrors(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(StatusCode.BAD_REQUEST), is(leftRestError));
    }

    @Test
    public void mergeRestErrorsLeftAndRightEmpty() {
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> left = new HashMap<>();
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> right = new HashMap<>();

        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> actual = subject.mergeRestErrors(left, right);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
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
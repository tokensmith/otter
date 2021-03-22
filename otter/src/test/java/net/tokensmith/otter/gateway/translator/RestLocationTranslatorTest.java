package net.tokensmith.otter.gateway.translator;

import helper.FixtureFactory;
import helper.entity.ClientErrorRestResource;
import helper.entity.ServerErrorRestResource;
import helper.entity.model.DummyErrorPayload;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.json.JsonDispatchErrorRouteRun;
import net.tokensmith.otter.dispatch.json.JsonRouteRun;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.entity.rest.RestTarget;
import net.tokensmith.otter.router.entity.Location;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.factory.RestBetweenFlyweight;
import net.tokensmith.otter.security.builder.entity.RestBetweens;
import net.tokensmith.otter.translatable.Translatable;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RestLocationTranslatorTest {
    private RestLocationTranslator<DummySession, DummyUser, DummyPayload> subject;
    @Mock
    private RestBetweenFlyweight<DummySession, DummyUser> mockRestBetweenFlyweight;
    @Mock
    private Validate mockValidate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> restErrors = new HashMap<>();
        restErrors.put(StatusCode.BAD_REQUEST, new RestError<>(DummyErrorPayload.class, new ClientErrorRestResource()));

        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> defaultErrors = new HashMap<>();
        Map<StatusCode, RestErrorTarget<DummySession, DummyUser, ? extends Translatable>> dispatchErrors = new HashMap<>();
        Map<StatusCode, RestErrorTarget<DummySession, DummyUser, ? extends Translatable>> defaultDispatchTargets = new HashMap<>();

        subject = new RestLocationTranslator<DummySession, DummyUser, DummyPayload>(
                mockRestBetweenFlyweight, restErrors, defaultErrors, dispatchErrors, defaultDispatchTargets, mockValidate
        );
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
        RestBetweens<DummySession, DummyUser> betweens = FixtureFactory.makeRestBetweens();
        when(mockRestBetweenFlyweight.make(any(), any())).thenReturn(betweens);

        RestTarget<DummySession, DummyUser, DummyPayload> target = FixtureFactory.makeRestTarget();

        Map<Method, Location> actual =  subject.to(target);

        assertThat(actual.size(), is(2));

        // GET
        assertThat(actual.get(Method.GET).getRouteRunner(), Is.is(notNullValue()));

        assertThat(actual.get(Method.GET).getPattern(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes().size(), Is.is(1));
        assertThat(actual.get(Method.GET).getAccepts(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getAccepts().size(), Is.is(1));

        assertThat(actual.get(Method.GET).getErrorRouteRunners(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getErrorRouteRunners().size(), Is.is(1));

        JsonRouteRun<DummySession, DummyUser, DummyPayload> getRouteRunner = (JsonRouteRun<DummySession, DummyUser, DummyPayload>) actual.get(Method.GET).getRouteRunner();

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
        assertThat(actual.get(Method.GET).getAccepts(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getAccepts().size(), Is.is(1));


        assertThat(actual.get(Method.POST).getErrorRouteRunners(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getErrorRouteRunners().size(), Is.is(1));

        JsonRouteRun<DummySession, DummyUser, DummyPayload> postRouteRunner = (JsonRouteRun<DummySession, DummyUser, DummyPayload>) actual.get(Method.POST).getRouteRunner();

        // should have the default mock validate.
        assertThat(postRouteRunner.getValidate(), is(mockValidate));

        // ordering of before.
        assertThat(postRouteRunner.getRestRoute().getBefore().get(0), is(betweens.getBefore().get(0)));
        assertThat(postRouteRunner.getRestRoute().getBefore().get(1), is(target.getBefore().get(0)));
        assertThat(postRouteRunner.getRestRoute().getBefore().get(2), is(target.getBefore().get(1)));

        // ordering of after.
        assertThat(postRouteRunner.getRestRoute().getAfter().get(0), is(betweens.getAfter().get(0)));
        assertThat(postRouteRunner.getRestRoute().getAfter().get(1), is(target.getAfter().get(0)));
        assertThat(postRouteRunner.getRestRoute().getAfter().get(2), is(target.getAfter().get(1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toNotFoundShouldBeOk() {
        RestBetweens<DummySession, DummyUser> betweens = FixtureFactory.makeRestBetweens();
        when(mockRestBetweenFlyweight.make(any(), any())).thenReturn(betweens);

        RestTarget<DummySession, DummyUser, DummyPayload> target = FixtureFactory.makeRestTarget();

        Map<Method, Location> actual =  subject.toNotFound(target);

        assertThat(actual.size(), is(2));

        // GET
        assertThat(actual.get(Method.GET).getRouteRunner(), Is.is(notNullValue()));

        assertThat(actual.get(Method.GET).getPattern(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes().size(), Is.is(1));
        assertThat(actual.get(Method.GET).getAccepts(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getAccepts().size(), Is.is(1));

        assertThat(actual.get(Method.GET).getErrorRouteRunners(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getErrorRouteRunners().size(), Is.is(1));

        JsonDispatchErrorRouteRun<DummySession, DummyUser, DummyPayload> getRouteRunner = (JsonDispatchErrorRouteRun<DummySession, DummyUser, DummyPayload>) actual.get(Method.GET).getRouteRunner();

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
        assertThat(actual.get(Method.GET).getAccepts(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getAccepts().size(), Is.is(1));

        assertThat(actual.get(Method.POST).getErrorRouteRunners(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getErrorRouteRunners().size(), Is.is(1));

        JsonDispatchErrorRouteRun<DummySession, DummyUser, DummyPayload> postRouteRunner = (JsonDispatchErrorRouteRun<DummySession, DummyUser, DummyPayload>) actual.get(Method.POST).getRouteRunner();

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
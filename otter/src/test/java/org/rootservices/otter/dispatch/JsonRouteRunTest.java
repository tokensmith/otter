package org.rootservices.otter.dispatch;

import helper.FixtureFactory;
import helper.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.ClientError;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.error.BadRequestRestResource;
import org.rootservices.otter.dispatch.translator.RestErrorHandler;
import org.rootservices.otter.dispatch.translator.rest.*;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.config.TranslatorAppFactory;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class JsonRouteRunTest {
    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();
    private JsonRouteRun<DummyUser, DummyPayload> subject;

    @Before
    public void setUp() {
        RestResponseTranslator<DummyPayload> restResponseTranslator = new RestResponseTranslator<>();
        RestRequestTranslator<DummyUser, DummyPayload> restRequestTranslator = new RestRequestTranslator<>();
        RestBtwnRequestTranslator<DummyUser, DummyPayload> restBtwnRequestTranslator = new RestBtwnRequestTranslator<>();
        RestBtwnResponseTranslator<DummyPayload> restBtwnResponseTranslator = new RestBtwnResponseTranslator<>();
        JsonTranslator<DummyPayload> jsonTranslator = appFactory.jsonTranslator(DummyPayload.class);
        RestRoute<DummyUser, DummyPayload> restRoute = FixtureFactory.makeRestRoute();

        Map<StatusCode, RestErrorHandler<DummyUser>> errorHandlers = new HashMap<>();
        RestErrorHandler<DummyUser> errorHandler = new JsonErrorHandler<DummyUser, ClientError>(
                appFactory.jsonTranslator(ClientError.class),
                new BadRequestRestResource<DummyUser>(),
                new RestRequestTranslator<DummyUser, ClientError>(),
                new RestResponseTranslator<ClientError>()
        );
        errorHandlers.put(StatusCode.BAD_REQUEST, errorHandler);

        subject = new JsonRouteRun<DummyUser, DummyPayload>(
                restRoute,
                restResponseTranslator,
                restRequestTranslator,
                restBtwnRequestTranslator,
                restBtwnResponseTranslator,
                jsonTranslator,
                errorHandlers,
                new RestErrorRequestTranslator<>(),
                new RestErrorResponseTranslator()
        );
    }

    @Test
    public void whenPostShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.POST, StatusCode.CREATED, Optional.of(body));
    }

    @Test
    public void whenPutShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.PUT, StatusCode.OK, Optional.of(body));
    }

    @Test
    public void whenPatchShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.PATCH, StatusCode.OK, Optional.of(body));
    }

    @Test
    public void whenDeletedShouldReturnOk() throws Exception {
        testRun(Method.DELETE, StatusCode.OK, Optional.empty());
    }

    @Test
    public void whenConnectShouldReturnOk() throws Exception {
        testRun(Method.CONNECT, StatusCode.OK, Optional.empty());
    }

    @Test
    public void whenOptionsShouldReturnOk() throws Exception {
        testRun(Method.OPTIONS, StatusCode.OK, Optional.empty());
    }

    @Test
    public void whenTraceShouldReturnOk() throws Exception {
        testRun(Method.TRACE, StatusCode.OK, Optional.empty());
    }

    @Test
    public void whenHeadShouldReturnOk() throws Exception {
        testRun(Method.HEAD, StatusCode.OK, Optional.empty());
    }

    public void testRun(Method method, StatusCode statusCode, Optional<byte[]> body) throws Exception {
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(method);
        ask.setBody(body);
        Answer answer = FixtureFactory.makeAnswer();

        Answer actual = subject.run(ask, answer);

        assertThat(actual.getStatusCode(), is(statusCode));
    }

    @Test
    public void whenPostAndDeserializationExceptionShouldReturnBadRequest() throws Exception {
        // Invalid Key
        String json = "{\"integer\": \"not a integer\", \"string\": \"foo\", \"local_date\": \"2019-01-01\"}";
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(Method.POST);
        ask.setBody(Optional.of(json.getBytes()));


        testRun(Method.HEAD, StatusCode.BAD_REQUEST, Optional.of(json.getBytes()));
    }

    @Test
    public void whenHaltExceptionShouldReturn403() {

        Ask ask = FixtureFactory.makeAsk();
        Answer answer = FixtureFactory.makeAnswer();

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);

        // add a before that will throw halt.
        HaltRestBetween haltBetween = new HaltRestBetween();
        subject.getRestRoute().getBefore().add(haltBetween);

        HaltException actual = null;
        try {
            subject.run(ask, answer);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(answer.getStatusCode(), is(StatusCode.UNAUTHORIZED));
    }
}
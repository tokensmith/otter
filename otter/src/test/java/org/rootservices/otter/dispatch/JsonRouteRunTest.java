package org.rootservices.otter.dispatch;

import helper.FixtureFactory;
import helper.entity.*;
import helper.entity.model.AlternatePayload;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class JsonRouteRunTest {
    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();


    public RestRoute<DummyUser, DummyPayload> rawPayloadRestResource() {
        JsonTranslator<DummyPayload> translator = appFactory.jsonTranslator(DummyPayload.class);
        RawPayloadRestResource rawPayloadRestResource = new RawPayloadRestResource(translator);
        return new RestRoute<>(
                rawPayloadRestResource, new ArrayList<>(), new ArrayList<>()
        );
    }

    public RestRoute<DummyUser, DummyPayload> okRestRoute() {
        return FixtureFactory.makeRestRoute();
    }

    public JsonRouteRun<DummyUser, DummyPayload> subject(RestRoute<DummyUser, DummyPayload> route) {
        RestResponseTranslator<DummyPayload> restResponseTranslator = new RestResponseTranslator<>();
        RestRequestTranslator<DummyUser, DummyPayload> restRequestTranslator = new RestRequestTranslator<>();
        RestBtwnRequestTranslator<DummyUser, DummyPayload> restBtwnRequestTranslator = new RestBtwnRequestTranslator<>();
        RestBtwnResponseTranslator<DummyPayload> restBtwnResponseTranslator = new RestBtwnResponseTranslator<>();
        JsonTranslator<DummyPayload> jsonTranslator = appFactory.jsonTranslator(DummyPayload.class);

        Map<StatusCode, RestErrorHandler<DummyUser>> errorHandlers = new HashMap<>();
        RestErrorHandler<DummyUser> errorHandler = new JsonErrorHandler<>(
                appFactory.jsonTranslator(ClientError.class),
                new BadRequestRestResource<>(),
                new RestRequestTranslator<>(),
                new RestResponseTranslator<>()
        );
        errorHandlers.put(StatusCode.BAD_REQUEST, errorHandler);

        JsonRouteRun<DummyUser, DummyPayload> subject = new JsonRouteRun<>(
                route,
                restResponseTranslator,
                restRequestTranslator,
                restBtwnRequestTranslator,
                restBtwnResponseTranslator,
                jsonTranslator,
                errorHandlers,
                new RestErrorRequestTranslator<>(),
                new RestErrorResponseTranslator()
        );
        return subject;
    }

    @Test
    public void whenGetShouldReturnOk() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.GET, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenPostShouldReturnOk() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(route, Method.POST, StatusCode.CREATED, Optional.of(body), Optional.empty());
    }

    @Test
    public void whenPutShouldReturnOk() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(route, Method.PUT, StatusCode.OK, Optional.of(body), Optional.empty());
    }

    @Test
    public void whenPatchShouldReturnOk() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(route, Method.PATCH, StatusCode.OK, Optional.of(body), Optional.empty());
    }

    @Test
    public void whenDeletedShouldReturnOk() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.DELETE, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenConnectShouldReturnOk() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.CONNECT, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenOptionsShouldReturnOk() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.OPTIONS, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenTraceShouldReturnOk() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.TRACE, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenHeadShouldReturnOk() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.HEAD, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    // raw response tests
    @Test
    public void whenGetShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.GET, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenPostShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = rawPayloadRestResource();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.POST, StatusCode.CREATED, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenPutShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = rawPayloadRestResource();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.PUT, StatusCode.OK, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenPatchShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = rawPayloadRestResource();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.PATCH, StatusCode.OK, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenDeletedShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.DELETE, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenConnectShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.CONNECT, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenOptionsShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.OPTIONS, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenTraceShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.TRACE, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenHeadShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.HEAD, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    public void testRun(RestRoute<DummyUser, DummyPayload> route, Method method, StatusCode statusCode, Optional<byte[]> body, Optional<byte[]> response) throws Exception {
        JsonRouteRun<DummyUser, DummyPayload> subject = subject(route);

        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(method);
        ask.setBody(body);
        Answer answer = FixtureFactory.makeAnswer();

        Answer actual = subject.run(ask, answer);

        assertThat(actual.getStatusCode(), is(statusCode));

        assertThat(actual.getPayload().isPresent(), is(response.isPresent()));

        if (actual.getPayload().isPresent()) {
            var actualPayload = new String(actual.getPayload().get());
            var expectedPayload = new String(response.get());
            assertThat(actualPayload, is(expectedPayload));
        }
    }

    @Test
    public void whenPostAndDeserializationExceptionShouldReturnBadRequest() throws Exception {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        // Invalid Key
        String json = "{\"integer\": \"not a integer\", \"string\": \"foo\", \"local_date\": \"2019-01-01\"}";
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(Method.POST);
        ask.setBody(Optional.of(json.getBytes()));


        byte[] response = "{\"source\":\"BODY\",\"key\":\"integer\",\"actual\":null,\"expected\":null,\"reason\":\"There was a invalid value for a key.\"}".getBytes();
        testRun(route, Method.POST, StatusCode.BAD_REQUEST, Optional.of(json.getBytes()), Optional.of(response));
    }

    @Test
    public void whenHaltExceptionShouldReturn403() {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        JsonRouteRun<DummyUser, DummyPayload> subject = subject(route);

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

    @Test
    public void payloadToBytesWhenNoPayloadShouldBeEmpty() {
        RestRoute<DummyUser, DummyPayload> route = okRestRoute();
        JsonRouteRun<DummyUser, DummyPayload> subject = subject(route);

        Optional<byte[]> actual = subject.payloadToBytes(Optional.empty());
        assertThat(actual.isPresent(), is(false));
    }
}
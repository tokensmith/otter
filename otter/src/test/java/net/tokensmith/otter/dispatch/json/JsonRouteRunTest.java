package net.tokensmith.otter.dispatch.json;

import helper.FixtureFactory;
import helper.entity.*;
import helper.entity.model.AlternatePayload;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.config.OtterAppFactory;
import org.junit.Test;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.error.rest.BadRequestRestResource;
import net.tokensmith.otter.dispatch.translator.RestErrorHandler;
import net.tokensmith.otter.dispatch.translator.rest.*;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.RestRoute;
import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.router.entity.io.Ask;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class JsonRouteRunTest {
    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();


    public RestRoute<DummySession, DummyUser, DummyPayload> rawPayloadRestResource() {
        JsonTranslator<AlternatePayload> translator = appFactory.jsonTranslator(AlternatePayload.class);
        RawPayloadRestResource rawPayloadRestResource = new RawPayloadRestResource(translator);
        return new RestRoute<>(
                rawPayloadRestResource, new ArrayList<>(), new ArrayList<>()
        );
    }

    public RestRoute<DummySession, DummyUser, DummyPayload> okRestRoute() {
        return FixtureFactory.makeRestRoute();
    }

    public JsonRouteRun<DummySession, DummyUser, DummyPayload> subject(RestRoute<DummySession, DummyUser, DummyPayload> route) {
        RestResponseTranslator<DummyPayload> restResponseTranslator = new RestResponseTranslator<>();
        RestRequestTranslator<DummySession, DummyUser, DummyPayload> restRequestTranslator = new RestRequestTranslator<>();
        RestBtwnRequestTranslator<DummySession, DummyUser, DummyPayload> restBtwnRequestTranslator = new RestBtwnRequestTranslator<>();
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

        JsonRouteRun<DummySession, DummyUser, DummyPayload> subject = new JsonRouteRun<>(
                route,
                restResponseTranslator,
                restRequestTranslator,
                restBtwnRequestTranslator,
                restBtwnResponseTranslator,
                jsonTranslator,
                new OtterAppFactory().restValidate(),
                errorHandlers,
                new RestErrorRequestTranslator<>(),
                new RestErrorResponseTranslator()
        );
        return subject;
    }

    @Test
    public void whenGetShouldReturnOk() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.GET, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenPostShouldReturnOk() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(route, Method.POST, StatusCode.CREATED, Optional.of(body), Optional.empty());
    }

    @Test
    public void whenPutShouldReturnOk() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(route, Method.PUT, StatusCode.OK, Optional.of(body), Optional.empty());
    }

    @Test
    public void whenPatchShouldReturnOk() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(route, Method.PATCH, StatusCode.OK, Optional.of(body), Optional.empty());
    }

    @Test
    public void whenDeletedShouldReturnOk() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.DELETE, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenConnectShouldReturnOk() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.CONNECT, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenOptionsShouldReturnOk() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.OPTIONS, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenTraceShouldReturnOk() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.TRACE, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    @Test
    public void whenHeadShouldReturnOk() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        testRun(route, Method.HEAD, StatusCode.OK, Optional.empty(), Optional.empty());
    }

    // raw response tests
    @Test
    public void whenGetShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.GET, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenPostShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = rawPayloadRestResource();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.POST, StatusCode.CREATED, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenPutShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = rawPayloadRestResource();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.PUT, StatusCode.OK, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenPatchShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = rawPayloadRestResource();
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.PATCH, StatusCode.OK, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenDeletedShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.DELETE, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenConnectShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.CONNECT, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenOptionsShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.OPTIONS, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenTraceShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.TRACE, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenHeadShouldReturnOkAndRawResponse() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(route, Method.HEAD, StatusCode.OK, Optional.empty(), Optional.of(response));
    }

    public void testRun(RestRoute<DummySession, DummyUser, DummyPayload> route, Method method, StatusCode statusCode, Optional<byte[]> body, Optional<byte[]> response) throws Exception {
        JsonRouteRun<DummySession, DummyUser, DummyPayload> subject = subject(route);

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
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        // Invalid Key
        String json = "{\"integer\": \"not a integer\", \"string\": \"foo\", \"local_date\": \"2019-01-01\"}";
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(Method.POST);
        ask.setBody(Optional.of(json.getBytes()));

        byte[] response = "{\"causes\":[{\"source\":\"BODY\",\"key\":\"integer\",\"actual\":\"not a integer\",\"expected\":[],\"reason\":\"There was a invalid value for a key.\"}]}".getBytes();
        testRun(route, Method.POST, StatusCode.BAD_REQUEST, Optional.of(json.getBytes()), Optional.of(response));
    }

    @Test
    public void whenPostAndValidationErrorsShouldReturnBadRequest() throws Exception {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        // Invalid Key
        String json = "{\"string\": \"foo\", \"local_date\": \"2019-01-01\"}";
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(Method.POST);
        ask.setBody(Optional.of(json.getBytes()));

        byte[] response = "{\"causes\":[{\"source\":\"BODY\",\"key\":\"integer\",\"actual\":null,\"expected\":[],\"reason\":\"must not be null\"}]}".getBytes();
        testRun(route, Method.POST, StatusCode.BAD_REQUEST, Optional.of(json.getBytes()), Optional.of(response));
    }

    @Test
    public void whenHaltExceptionShouldReturn403() {
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        JsonRouteRun<DummySession, DummyUser, DummyPayload> subject = subject(route);

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
        RestRoute<DummySession, DummyUser, DummyPayload> route = okRestRoute();
        JsonRouteRun<DummySession, DummyUser, DummyPayload> subject = subject(route);

        Optional<byte[]> actual = subject.payloadToBytes(Optional.empty());
        assertThat(actual.isPresent(), is(false));
    }
}
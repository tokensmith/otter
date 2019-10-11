package org.rootservices.otter.dispatch;

import com.fasterxml.jackson.databind.ObjectReader;
import helper.FixtureFactory;
import helper.entity.ClientErrorRestResource;
import helper.entity.RawPayloadErrorRestResource;
import helper.entity.RawPayloadRestResource;
import helper.entity.model.AlternatePayload;
import helper.entity.model.DummyErrorPayload;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.dispatch.entity.RestErrorResponse;
import org.rootservices.otter.dispatch.translator.rest.*;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.config.TranslatorAppFactory;
import org.rootservices.otter.translator.exception.DeserializationException;
import org.rootservices.otter.translator.exception.InvalidValueException;
import org.rootservices.otter.translator.exception.Reason;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class JsonErrorHandlerTest {

    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();

    public RestResource<DummyUser, DummyErrorPayload> rawPayloadRestResource() {
        JsonTranslator<AlternatePayload> translator = appFactory.jsonTranslator(AlternatePayload.class);
        return new RawPayloadErrorRestResource(translator);
    }

    private RestResource<DummyUser, DummyErrorPayload> clientErrorRestResource() {
        return new ClientErrorRestResource();
    }

    public JsonErrorHandler<DummyUser, DummyErrorPayload> subject(RestResource<DummyUser, DummyErrorPayload> restResource) {
        JsonTranslator<DummyErrorPayload> jsonTranslator = appFactory.jsonTranslator(DummyErrorPayload.class);

        JsonErrorHandler<DummyUser, DummyErrorPayload> subject = new JsonErrorHandler<>(
                jsonTranslator,
                restResource,
                new RestRequestTranslator<>(),
                new RestResponseTranslator<>()

        );
        return subject;
    }

    public Optional<byte[]> clientErrorResponse() {
        return Optional.of("{\"error\":\"invalid value\",\"description\":\"the key, {} had a invalid value\"}".getBytes());
    }

    @Test
    public void whenGetShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        Optional<byte[]> response = clientErrorResponse();
        testRun(restResource, Method.GET, StatusCode.BAD_REQUEST, Optional.of(body), response);
    }

    @Test
    public void whenPostShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        Optional<byte[]> response = clientErrorResponse();
        testRun(restResource, Method.POST, StatusCode.BAD_REQUEST, Optional.of(body), response);
    }

    @Test
    public void whenPutShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        Optional<byte[]> response = clientErrorResponse();
        testRun(restResource, Method.PUT, StatusCode.BAD_REQUEST, Optional.of(body), response);
    }

    @Test
    public void whenPatchShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        Optional<byte[]> response = clientErrorResponse();
        testRun(restResource, Method.PATCH, StatusCode.BAD_REQUEST, Optional.of(body), response);
    }

    @Test
    public void whenDeletedShouldReturnOk() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        Optional<byte[]> response = clientErrorResponse();
        testRun(restResource, Method.DELETE, StatusCode.BAD_REQUEST, Optional.empty(), response);
    }

    @Test
    public void whenConnectShouldReturnOk() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        Optional<byte[]> response = clientErrorResponse();
        testRun(restResource, Method.CONNECT, StatusCode.BAD_REQUEST, Optional.empty(), response);
    }

    @Test
    public void whenOptionsShouldReturnOk() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        Optional<byte[]> response = clientErrorResponse();
        testRun(restResource, Method.OPTIONS, StatusCode.BAD_REQUEST, Optional.empty(), response);
    }

    @Test
    public void whenTraceShouldReturnOk() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        Optional<byte[]> response = clientErrorResponse();
        testRun(restResource, Method.TRACE, StatusCode.BAD_REQUEST, Optional.empty(), response);
    }

    @Test
    public void whenHeadShouldReturnOk() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        Optional<byte[]> response = clientErrorResponse();
        testRun(restResource, Method.HEAD, StatusCode.BAD_REQUEST, Optional.empty(), response);
    }

    // raw payload tests
    @Test
    public void whenGetShouldReturnOkAndRawResponse() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        RestResource<DummyUser, DummyErrorPayload> restResource = rawPayloadRestResource();
        testRun(restResource, Method.GET, StatusCode.BAD_REQUEST, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenPostShouldReturnOkAndRawResponse() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        RestResource<DummyUser, DummyErrorPayload> restResource = rawPayloadRestResource();
        testRun(restResource, Method.POST, StatusCode.BAD_REQUEST, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenPutShouldReturnOkAndRawResponse() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        RestResource<DummyUser, DummyErrorPayload> restResource = rawPayloadRestResource();
        testRun(restResource, Method.PUT, StatusCode.BAD_REQUEST, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenPatchShouldReturnOkAndRawResponse() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        RestResource<DummyUser, DummyErrorPayload> restResource = rawPayloadRestResource();
        testRun(restResource, Method.PATCH, StatusCode.BAD_REQUEST, Optional.of(body), Optional.of(response));
    }

    @Test
    public void whenDeletedShouldReturnOkAndRawResponse() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(restResource, Method.DELETE, StatusCode.BAD_REQUEST, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenConnectShouldReturnOkAndRawResponse() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(restResource, Method.CONNECT, StatusCode.BAD_REQUEST, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenOptionsShouldReturnOkAndRawResponse() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(restResource, Method.OPTIONS, StatusCode.BAD_REQUEST, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenTraceShouldReturnOkAndRawResponse() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(restResource, Method.TRACE, StatusCode.BAD_REQUEST, Optional.empty(), Optional.of(response));
    }

    @Test
    public void whenHeadShouldReturnOkAndRawResponse() throws Exception {
        RestResource<DummyUser, DummyErrorPayload> restResource = rawPayloadRestResource();
        byte[] response = "{\"id\":\"b5b24f75-7c7a-453f-a574-1bae3d6820a7\"}".getBytes();
        testRun(restResource, Method.HEAD, StatusCode.BAD_REQUEST, Optional.empty(), Optional.of(response));
    }

    public void testRun(RestResource<DummyUser, DummyErrorPayload> restResource, Method method, StatusCode statusCode, Optional<byte[]> body, Optional<byte[]> expectedResponse) throws IOException {
        RestErrorRequest<DummyUser> request = FixtureFactory.makeRestErrorRequest();
        request.setBody(body);
        request.setMethod(method);

        RestErrorResponse response = FixtureFactory.makeRestErrorResponse();

        InvalidValueException ive = new InvalidValueException("", null, "id", "not and integer");
        DeserializationException cause = new DeserializationException("", "id", null, Reason.INVALID_VALUE, ive);

        JsonErrorHandler<DummyUser, DummyErrorPayload> subject = subject(restResource);
        Answer actual = subject.run(request, response, cause);

        assertThat(actual.getStatusCode(), is(statusCode));

        assertThat(actual.getPayload().isPresent(), is(expectedResponse.isPresent()));

        if (actual.getPayload().isPresent()) {
            var actualPayload = new String(actual.getPayload().get());
            var expectedPayload = new String(expectedResponse.get());
            assertThat(actualPayload, is(expectedPayload));
        }
    }

    @Test
    public void payloadToBytesWhenNoPayloadShouldBeEmpty() {
        RestResource<DummyUser, DummyErrorPayload> restResource = clientErrorRestResource();
        JsonErrorHandler<DummyUser, DummyErrorPayload> subject = subject(restResource);

        Optional<byte[]> actual = subject.payloadToBytes(Optional.empty());
        assertThat(actual.isPresent(), is(false));
    }
}
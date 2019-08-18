package org.rootservices.otter.dispatch;

import com.fasterxml.jackson.databind.ObjectReader;
import helper.FixtureFactory;
import helper.entity.ClientErrorRestResource;
import helper.entity.DummyErrorPayload;
import helper.entity.DummyPayload;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.dispatch.entity.RestErrorResponse;
import org.rootservices.otter.dispatch.translator.rest.*;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.config.TranslatorAppFactory;
import org.rootservices.otter.translator.exception.DeserializationException;
import org.rootservices.otter.translator.exception.InvalidValueException;
import org.rootservices.otter.translator.exception.Reason;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class JsonErrorHandlerTest {

    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();
    private JsonErrorHandler<DummyUser, DummyErrorPayload> subject;

    @Before
    public void setUp() {
        JsonTranslator<DummyErrorPayload> jsonTranslator = appFactory.jsonTranslator(DummyErrorPayload.class);

        ClientErrorRestResource errorRestResource = new ClientErrorRestResource();
        subject = new JsonErrorHandler<>(
                jsonTranslator,
                errorRestResource,
                new RestRequestTranslator<>(),
                new RestResponseTranslator<>()

        );
    }

    @Test
    public void whenGetShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.GET, StatusCode.BAD_REQUEST, Optional.of(body));
    }

    @Test
    public void whenPostShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.POST, StatusCode.BAD_REQUEST, Optional.of(body));
    }

    @Test
    public void whenPutShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.PUT, StatusCode.BAD_REQUEST, Optional.of(body));
    }

    @Test
    public void whenPatchShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.PATCH, StatusCode.BAD_REQUEST, Optional.of(body));
    }

    @Test
    public void whenDeletedShouldReturnOk() throws Exception {
        testRun(Method.DELETE, StatusCode.BAD_REQUEST, Optional.empty());
    }

    @Test
    public void whenConnectShouldReturnOk() throws Exception {
        testRun(Method.CONNECT, StatusCode.BAD_REQUEST, Optional.empty());
    }

    @Test
    public void whenOptionsShouldReturnOk() throws Exception {
        testRun(Method.OPTIONS, StatusCode.BAD_REQUEST, Optional.empty());
    }

    @Test
    public void whenTraceShouldReturnOk() throws Exception {
        testRun(Method.TRACE, StatusCode.BAD_REQUEST, Optional.empty());
    }

    @Test
    public void whenHeadShouldReturnOk() throws Exception {
        testRun(Method.HEAD, StatusCode.BAD_REQUEST, Optional.empty());
    }

    public void testRun(Method method, StatusCode statusCode, Optional<byte[]> body) throws IOException {
        RestErrorRequest<DummyUser> request = FixtureFactory.makeRestErrorRequest();
        request.setBody(body);
        request.setMethod(method);

        RestErrorResponse response = FixtureFactory.makeRestErrorResponse();

        InvalidValueException ive = new InvalidValueException("", null, "id");
        DeserializationException cause = new DeserializationException("", "id", Reason.INVALID_VALUE, ive);

        Answer actual = subject.run(request, response, cause);

        assertThat(actual.getStatusCode(), is(statusCode));
        assertThat(actual.getPayload().isPresent(), is(true));

        ObjectReader reader = appFactory
                .objectReader()
                .forType(DummyErrorPayload.class);

        DummyErrorPayload actualPayload = reader.readValue(actual.getPayload().get());

        assertThat(actualPayload, is(notNullValue()));
        assertThat(actualPayload.getError(), is(notNullValue()));
        assertThat(actualPayload.getDescription(), is(notNullValue()));
    }
}
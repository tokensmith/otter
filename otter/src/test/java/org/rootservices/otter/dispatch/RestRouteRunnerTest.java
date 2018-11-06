package org.rootservices.otter.dispatch;

import helper.FixtureFactory;
import helper.entity.DummyPayload;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.entity.ErrorPayload;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class RestRouteRunnerTest {
    @Mock
    private JsonTranslator<DummyPayload> mockJsonTranslator;
    @Mock
    private RequestTranslator<DummySession, DummyUser, DummyPayload> mockRequestTranslator;
    @Mock
    private AnswerTranslator<DummySession> mockAnswerTranslator;

    private RestRouteRunner<DummySession, DummyUser, DummyPayload> subject;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Route<DummySession, DummyUser, DummyPayload> route = FixtureFactory.makeRestRoute();

        subject = new RestRouteRunner<DummySession, DummyUser, DummyPayload>(
                mockJsonTranslator, route, mockRequestTranslator, mockAnswerTranslator
        );
    }

    public Optional<byte[]> makeBody() {
        String body = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";
        return Optional.of(body.getBytes());
    }

    @Test
    public void parsePayloadWhenDuplicateKeyExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        DuplicateKeyException duplicateKeyException = new DuplicateKeyException("test", null, "key");
        doThrow(duplicateKeyException).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        HaltException actual = null;
        try {
            subject.parsePayload(request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));

        assertThat(response.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(response.getHeaders(), is(notNullValue()));
        assertThat(response.getHeaders().size(), is(0));
        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getPayload().isPresent(), is(true));
        assertThat(response.getTemplate().isPresent(), is(false));
        assertThat(response.getPresenter().isPresent(), is(false));
    }

    @Test
    public void parsePayloadWhenInvalidValueExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidValueException invalidValueException = new InvalidValueException("test", null, "key");
        doThrow(invalidValueException).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        HaltException actual = null;
        try {
            subject.parsePayload(request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));

        assertThat(response.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(response.getHeaders(), is(notNullValue()));
        assertThat(response.getHeaders().size(), is(0));
        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getPayload().isPresent(), is(true));
        assertThat(response.getTemplate().isPresent(), is(false));
        assertThat(response.getPresenter().isPresent(), is(false));
    }

    @Test
    public void parsePayloadWhenUnknownKeyExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        UnknownKeyException unknownKeyException = new UnknownKeyException("test", null, "key");
        doThrow(unknownKeyException).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        HaltException actual = null;
        try {
            subject.parsePayload(request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));

        assertThat(response.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(response.getHeaders(), is(notNullValue()));
        assertThat(response.getHeaders().size(), is(0));
        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getPayload().isPresent(), is(true));
        assertThat(response.getTemplate().isPresent(), is(false));
        assertThat(response.getPresenter().isPresent(), is(false));
    }

    @Test
    public void parsePayloadWhenInvalidPayloadExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidPayloadException invalidPayloadException = new InvalidPayloadException("test", null);
        doThrow(invalidPayloadException).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        HaltException actual = null;
        try {
            subject.parsePayload(request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));

        assertThat(response.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(response.getHeaders(), is(notNullValue()));
        assertThat(response.getHeaders().size(), is(0));
        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getPayload().isPresent(), is(true));
        assertThat(response.getTemplate().isPresent(), is(false));
        assertThat(response.getPresenter().isPresent(), is(false));
    }

    @Test
    public void parsePayloadWhenToJsonExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidPayloadException invalidPayloadException = new InvalidPayloadException("test", null);
        doThrow(invalidPayloadException).when(mockJsonTranslator).from(request.getBody().get());

        ToJsonException toJsonException = new ToJsonException("test", null);
        doThrow(toJsonException).when(mockJsonTranslator).to(any(ErrorPayload.class));

        HaltException actual = null;
        try {
            subject.parsePayload(request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));

        assertThat(response.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(response.getHeaders(), is(notNullValue()));
        assertThat(response.getHeaders().size(), is(0));
        assertThat(response.getCookies(), is(notNullValue()));
        assertThat(response.getPayload().isPresent(), is(false));
        assertThat(response.getTemplate().isPresent(), is(false));
        assertThat(response.getPresenter().isPresent(), is(false));
    }

}
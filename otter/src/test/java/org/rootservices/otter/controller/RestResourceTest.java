package org.rootservices.otter.controller;


import helper.FixtureFactory;
import helper.entity.DummyPayload;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.FakeRestResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.entity.ErrorPayload;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
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


public class RestResourceTest {
    @Mock
    private JsonTranslator<DummyPayload> mockJsonTranslator;
    private RestResource<DummyPayload, DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new FakeRestResource(mockJsonTranslator);
    }

    public Optional<byte[]> makeBody() {
        String body = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";
        return Optional.of(body.getBytes());
    }

    @Test
    public void getShouldBeNotImplemented() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.get(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void postShouldBeNotImplemented() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        DummyPayload dummy = new DummyPayload();
        when(mockJsonTranslator.from(request.getBody().get())).thenReturn(dummy);

        Response<DummySession> actual = subject.post(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void postWhenDuplicateKeyExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        DuplicateKeyException e = new DuplicateKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.post(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void postWhenInvalidValueExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidValueException e = new InvalidValueException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.post(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void postWhenUnknownKeyExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        UnknownKeyException e = new UnknownKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.post(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void postWhenInvalidPayloadExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.post(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void postWhenToJsonExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ToJsonException e2 = new ToJsonException("test", null);
        doThrow(e2).when(mockJsonTranslator).to(any(ErrorPayload.class));

        Response<DummySession> actual = subject.post(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void putShouldBeNotImplemented() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        DummyPayload dummy = new DummyPayload();
        when(mockJsonTranslator.from(request.getBody().get())).thenReturn(dummy);

        Response<DummySession> actual = subject.put(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void putWhenDuplicateKeyExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        DuplicateKeyException e = new DuplicateKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.put(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void putWhenInvalidValueExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidValueException e = new InvalidValueException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.put(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void putWhenUnknownKeyExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        UnknownKeyException e = new UnknownKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.put(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void putWhenInvalidPayloadExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.put(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void putWhenToJsonExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ToJsonException e2 = new ToJsonException("test", null);
        doThrow(e2).when(mockJsonTranslator).to(any(ErrorPayload.class));

        Response<DummySession> actual = subject.put(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void deleteShouldBeNotImplemented() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.delete(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void connectShouldBeNotImplemented() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.connect(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void optionsShouldBeNotImplemented() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.options(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void traceShouldBeNotImplemented() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.trace(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void patchShouldBeNotImplemented() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        DummyPayload dummy = new DummyPayload();
        when(mockJsonTranslator.from(request.getBody().get())).thenReturn(dummy);

        Response<DummySession> actual = subject.patch(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void patchWhenDuplicateKeyExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        DuplicateKeyException e = new DuplicateKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.patch(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void patchWhenInvalidValueExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidValueException e = new InvalidValueException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.patch(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void patchWhenUnknownKeyExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        UnknownKeyException e = new UnknownKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response<DummySession> actual = subject.patch(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void patchWhenInvalidPayloadExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(mockJsonTranslator.to(any(ErrorPayload.class))).thenReturn(out);

        Response actual = subject.patch(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

    @Test
    public void patchWhenToJsonExceptionShouldBeBadRequest() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response<DummySession> response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody().get());

        ToJsonException e2 = new ToJsonException("test", null);
        doThrow(e2).when(mockJsonTranslator).to(any(ErrorPayload.class));

        Response actual = subject.patch(request, response);

        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
        assertThat(actual.getPresenter().isPresent(), is(false));
    }

}
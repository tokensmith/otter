package org.rootservices.otter.controller;


import helper.FixtureFactory;
import helper.entity.Dummy;
import helper.entity.FakeRestResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;

import java.io.BufferedReader;
import java.io.StringReader;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


public class RestResourceTest {
    @Mock
    private JsonTranslator mockJsonTranslator;
    private RestResource subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new FakeRestResource(mockJsonTranslator);
    }

    public BufferedReader makeBody() {
        StringReader sr = new StringReader("{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}");
        return new BufferedReader(sr);
    }

    @Test
    public void getTypeShouldBeOk() {
        Class actual = subject.getType();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void getShouldBeNotImplemented() throws Exception {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.get(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        Dummy dummy = new Dummy();
        when(mockJsonTranslator.from(request.getBody(), Dummy.class)).thenReturn(dummy);

        Response actual = subject.post(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        DuplicateKeyException e = new DuplicateKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

        Response actual = subject.post(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        InvalidValueException e = new InvalidValueException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

        Response actual = subject.post(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        UnknownKeyException e = new UnknownKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

        Response actual = subject.post(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

        Response actual = subject.post(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);

        ToJsonException e2 = new ToJsonException("test", null);
        doThrow(e2).when(mockJsonTranslator).to(any(Error.class));

        Response actual = subject.post(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        Dummy dummy = new Dummy();
        when(mockJsonTranslator.from(request.getBody(), Dummy.class)).thenReturn(dummy);

        Response actual = subject.put(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        DuplicateKeyException e = new DuplicateKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

        Response actual = subject.put(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        InvalidValueException e = new InvalidValueException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

        Response actual = subject.put(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        UnknownKeyException e = new UnknownKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

        Response actual = subject.put(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

        Response actual = subject.put(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);

        ToJsonException e2 = new ToJsonException("test", null);
        doThrow(e2).when(mockJsonTranslator).to(any(Error.class));

        Response actual = subject.put(request, response);

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
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.delete(request, response);

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
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.connect(request, response);

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
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.options(request, response);

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
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.trace(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        Dummy dummy = new Dummy();
        when(mockJsonTranslator.from(request.getBody(), Dummy.class)).thenReturn(dummy);

        Response actual = subject.patch(request, response);

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        DuplicateKeyException e = new DuplicateKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

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
    public void patchWhenInvalidValueExceptionShouldBeBadRequest() throws Exception {
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        InvalidValueException e = new InvalidValueException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

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
    public void patchWhenUnknownKeyExceptionShouldBeBadRequest() throws Exception {
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        UnknownKeyException e = new UnknownKeyException("test", null, "key");
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

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
    public void patchWhenInvalidPayloadExceptionShouldBeBadRequest() throws Exception {
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);
        when(mockJsonTranslator.to(any(Error.class))).thenReturn("");

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
        Request request = FixtureFactory.makeRequest();
        request.setBody(makeBody());

        Response response = FixtureFactory.makeResponse();

        InvalidPayloadException e = new InvalidPayloadException("test", null);
        doThrow(e).when(mockJsonTranslator).from(request.getBody(), Dummy.class);

        ToJsonException e2 = new ToJsonException("test", null);
        doThrow(e2).when(mockJsonTranslator).to(any(Error.class));

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
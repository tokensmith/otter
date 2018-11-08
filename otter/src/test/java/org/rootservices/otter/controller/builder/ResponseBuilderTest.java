package org.rootservices.otter.controller.builder;


import helper.entity.DummySession;
import helper.fake.FakePresenter;
import helper.FixtureFactory;
import org.junit.Test;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class ResponseBuilderTest {

    @Test
    public void headersShouldBeOk() {
        Map<String, String> headers = new HashMap<>();

        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.headers(headers).build();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void cookiesShouldBeOk() {
        Map<String, Cookie> cookies = FixtureFactory.makeCookies();

        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.cookies(cookies).build();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void bodyShouldBeOk() {
        Optional<ByteArrayOutputStream> body = Optional.empty();

        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.payload(body).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPayload(), is(body));
    }

    @Test
    public void templateShouldBeOk() {
        Optional<String> template = Optional.empty();

        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.template(template).build();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void presenterShouldBeOk() {
        Optional<Object> presenter = Optional.of(new FakePresenter());
        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.presenter(presenter).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPresenter().isPresent(), is(true));
        assertThat(actual.getPresenter().get(), is(presenter.get()));
    }

    @Test
    public void statusCodeShouldBeOk() {
        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.statusCode(StatusCode.CREATED).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.CREATED));
    }

    @Test
    public void notFoundShouldBeOk() {
        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.notFound().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_FOUND));
    }

    @Test
    public void notImplementedShouldBeOk() {
        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.notImplemented().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }


    @Test
    public void badRequestShouldBeOk() {
        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.badRequest().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
    }

    @Test
    public void unAuthorizedShouldBeOk() {
        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.unAuthorized().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.UNAUTHORIZED));
    }

    @Test
    public void serverErrorShouldBeOk() {
        ResponseBuilder<DummySession> subject = new ResponseBuilder<DummySession>();
        Response actual = subject.serverError().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.SERVER_ERROR));
    }

}
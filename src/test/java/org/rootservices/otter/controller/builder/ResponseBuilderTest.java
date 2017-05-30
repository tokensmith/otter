package org.rootservices.otter.controller.builder;


import helper.entity.FakePresenter;
import helper.FixtureFactory;
import org.junit.Test;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class ResponseBuilderTest {

    @Test
    public void setHeadersShouldBeOk() {
        Map<String, String> headers = new HashMap<>();

        ResponseBuilder subject = new ResponseBuilder();
        Response actual = subject.setHeaders(headers).build();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void setCookiesShouldBeOk() {
        Map<String, Cookie> cookies = FixtureFactory.makeCookies();

        ResponseBuilder subject = new ResponseBuilder();
        Response actual = subject.setCookies(cookies).build();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void setBodyShouldBeOk() {
        Optional<String> body = Optional.empty();

        ResponseBuilder subject = new ResponseBuilder();
        Response actual = subject.setBody(body).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getBody(), is(body));
    }

    @Test
    public void setTemplateShouldBeOk() {
        Optional<String> template = Optional.empty();

        ResponseBuilder subject = new ResponseBuilder();
        Response actual = subject.setTemplate(template).build();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void setPresenterShouldBeOk() {
        Optional<Object> presenter = Optional.of(new FakePresenter());
        ResponseBuilder subject = new ResponseBuilder();
        Response actual = subject.setPresenter(presenter).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPresenter().isPresent(), is(true));
        assertThat(actual.getPresenter().get(), is(presenter.get()));
    }

    @Test
    public void notFoundShouldBeOk() {
        ResponseBuilder subject = new ResponseBuilder();
        Response actual = subject.notFound().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_FOUND));
    }

    @Test
    public void notImplementedShouldBeOk() {
        ResponseBuilder subject = new ResponseBuilder();
        Response actual = subject.notImplemented().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }


    @Test
    public void badRequestShouldBeOk() {
        ResponseBuilder subject = new ResponseBuilder();
        Response actual = subject.badRequest().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
    }

}
package net.tokensmith.otter.router.builder;

import helper.FixtureFactory;
import helper.fake.FakePresenter;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.router.entity.io.Answer;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AnswerBuilderTest {
    
    @Test
    public void headersShouldBeOk() {
        Map<String, String> headers = new HashMap<>();

        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.headers(headers).build();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void cookiesShouldBeOk() {
        Map<String, Cookie> cookies = FixtureFactory.makeCookies();

        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.cookies(cookies).build();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void bodyShouldBeOk() {
        Optional<byte[]> body = Optional.empty();

        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.payload(body).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPayload(), is(body));
    }

    @Test
    public void templateShouldBeOk() {
        Optional<String> template = Optional.empty();

        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.template(template).build();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void presenterShouldBeOk() {
        Optional<Object> presenter = Optional.of(new FakePresenter());
        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.presenter(presenter).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPresenter().isPresent(), is(true));
        assertThat(actual.getPresenter().get(), is(presenter.get()));
    }

    @Test
    public void statusCodeShouldBeOk() {
        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.statusCode(StatusCode.CREATED).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.CREATED));
    }

    @Test
    public void notFoundShouldBeOk() {
        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.notFound().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_FOUND));
    }

    @Test
    public void notImplementedShouldBeOk() {
        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.notImplemented().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }


    @Test
    public void badRequestShouldBeOk() {
        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.badRequest().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST));
    }

    @Test
    public void unAuthorizedShouldBeOk() {
        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.unAuthorized().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.UNAUTHORIZED));
    }

    @Test
    public void serverErrorShouldBeOk() {
        AnswerBuilder subject = new AnswerBuilder();
        Answer actual = subject.serverError().build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.SERVER_ERROR));
    }


}
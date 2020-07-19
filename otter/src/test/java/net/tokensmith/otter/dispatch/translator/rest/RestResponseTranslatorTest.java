package net.tokensmith.otter.dispatch.translator.rest;

import helper.FixtureFactory;
import helper.entity.model.DummyPayload;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.dispatch.entity.RestErrorResponse;
import net.tokensmith.otter.router.entity.io.Answer;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class RestResponseTranslatorTest {
    private RestResponseTranslator<DummyPayload> subject;

    @Before
    public void setUp() {
        subject = new RestResponseTranslator<DummyPayload>();
    }

    @Test
    public void from() {
        RestResponse<DummyPayload> from = FixtureFactory.makeRestResponse();

        Answer actual = subject.from(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(from.getStatusCode()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getPayload().isPresent(), is(false));
    }

    @Test
    public void toWhenFromIsAnswer() {
        Answer from = FixtureFactory.makeAnswer();

        RestResponse<DummyPayload> actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(from.getStatusCode()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getPayload().isPresent(), is(false));
    }

    @Test
    public void toWhenFromIsRestBtwnResponse() {
        RestBtwnResponse from = FixtureFactory.makeRestBtwnResponse();

        RestResponse<DummyPayload> actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(from.getStatusCode()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getPayload().isPresent(), is(false));
    }

    @Test
    public void toWhenFromIsRestErrorResponse() {
        RestErrorResponse from = FixtureFactory.makeRestErrorResponse();

        RestResponse<DummyPayload> actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(from.getStatusCode()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getPayload().isPresent(), is(false));
    }
}
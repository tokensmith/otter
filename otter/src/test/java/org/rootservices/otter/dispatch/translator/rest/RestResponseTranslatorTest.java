package org.rootservices.otter.dispatch.translator.rest;

import helper.FixtureFactory;
import helper.entity.DummyPayload;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.router.entity.io.Answer;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class RestResponseTranslatorTest {
    private RestResponseTranslator<DummyPayload> subject;

    @Before
    public void setUp() throws Exception {
        subject = new RestResponseTranslator<DummyPayload>();
    }

    @Test
    public void to() {
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
    public void from() {
        Answer from = FixtureFactory.makeAnswer();

        RestResponse<DummyPayload> actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(from.getStatusCode()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getPayload().isPresent(), is(false));
    }
}
package org.rootservices.otter.dispatch.translator.rest;

import helper.FixtureFactory;
import helper.entity.DummyPayload;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.router.entity.io.Answer;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class RestBtwnResponseTranslatorTest {
    private RestBtwnResponseTranslator<DummyPayload> subject;

    @Before
    public void setUp() {
        subject = new RestBtwnResponseTranslator<DummyPayload>();
    }

    @Test
    public void toWhenFromIsAnswer() {
        Answer from = FixtureFactory.makeAnswer();

        RestBtwnResponse actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(from.getStatusCode()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getPayload().isPresent(), is(false));
    }

    @Test
    public void toWhenFromIsRestResponse() {
        RestResponse<DummyPayload> from = FixtureFactory.makeRestResponse();

        RestBtwnResponse actual = subject.to(from, Optional.empty());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(from.getStatusCode()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getPayload().isPresent(), is(false));
    }
}
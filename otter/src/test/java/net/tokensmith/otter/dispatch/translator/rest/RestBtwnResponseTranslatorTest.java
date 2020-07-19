package net.tokensmith.otter.dispatch.translator.rest;

import helper.FixtureFactory;
import helper.entity.model.DummyPayload;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.io.Answer;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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
        assertThat(actual.getRawPayload().isPresent(), is(false));
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
        assertThat(actual.getRawPayload().isPresent(), is(false));
    }
}
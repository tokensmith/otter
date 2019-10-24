package net.tokensmith.otter.dispatch.translator.rest;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.dispatch.entity.RestErrorResponse;
import net.tokensmith.otter.router.entity.io.Answer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class RestErrorResponseTranslatorTest {
    private RestErrorResponseTranslator subject;

    @Before
    public void setUp() {
        subject = new RestErrorResponseTranslator();
    }

    @Test
    public void toWhenFromIsAnswer() {
        Answer from = FixtureFactory.makeAnswer();

        RestErrorResponse actual = subject.to(from);

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

        RestErrorResponse actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(from.getStatusCode()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getPayload().isPresent(), is(false));
    }
}
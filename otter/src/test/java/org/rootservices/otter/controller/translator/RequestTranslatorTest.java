package org.rootservices.otter.controller.translator;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.entity.io.Ask;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class RequestTranslatorTest {
    private RequestTranslator<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        subject = new RequestTranslator<>();
    }

    @Test
    public void to() {
        Ask from = FixtureFactory.makeAsk();

        Request<DummySession, DummyUser> actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMatcher(), is(from.getMatcher()));
        assertThat(actual.getMethod(), is(from.getMethod()));
        assertThat(actual.getPathWithParams(), is(from.getPathWithParams()));
        assertThat(actual.getContentType(), is(from.getContentType()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getQueryParams(), is(from.getQueryParams()));
        assertThat(actual.getFormData(), is(from.getFormData()));
        assertThat(actual.getBody(), is(from.getBody()));
        assertThat(actual.getCsrfChallenge(), is(from.getCsrfChallenge()));
        assertThat(actual.getIpAddress(), is(from.getIpAddress()));
        assertThat(actual.getSession().isPresent(), is(false));
        assertThat(actual.getUser().isPresent(), is(false));
    }
}
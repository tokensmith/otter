package org.rootservices.otter.dispatch.translator;

import helper.FixtureFactory;
import helper.entity.DummySession;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.router.entity.io.Answer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class AnswerTranslatorTest {
    private AnswerTranslator<DummySession> subject;

    @Before
    public void setUp() throws Exception {
        subject = new AnswerTranslator<>();
    }

    @Test
    public void to() {
        Response<DummySession> from = FixtureFactory.makeResponse();

        Answer actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(from.getStatusCode()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getPayload(), is(from.getPayload()));
        assertThat(actual.getTemplate(), is(from.getTemplate()));
        assertThat(actual.getPresenter(), is(from.getPresenter()));
    }

    @Test
    public void from() {
        Answer to = FixtureFactory.makeAnswer();

        Response<DummySession> actual = subject.from(to);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(to.getStatusCode()));
        assertThat(actual.getHeaders(), is(to.getHeaders()));
        assertThat(actual.getCookies(), is(to.getCookies()));
        assertThat(actual.getHeaders(), is(to.getHeaders()));
        assertThat(actual.getPayload(), is(to.getPayload()));
        assertThat(actual.getTemplate(), is(to.getTemplate()));
        assertThat(actual.getPresenter(), is(to.getPresenter()));
        assertThat(actual.getSession().isPresent(), is(false));
    }
}
package org.rootservices.otter.dispatch.translator;

import helper.FixtureFactory;
import helper.entity.DummyPayload;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.dispatch.translator.rest.RestRequestTranslator;
import org.rootservices.otter.router.entity.io.Ask;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class RestRequestTranslatorTest {
    private RestRequestTranslator<DummyUser, DummyPayload> subject;

    @Before
    public void setUp() {
        subject = new RestRequestTranslator<>();
    }

    @Test
    public void to() {
        Ask from = FixtureFactory.makeAsk();

        RestRequest<DummyUser, DummyPayload> actual = subject.to(from);

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
        assertThat(actual.getIpAddress(), is(from.getIpAddress()));
        assertThat(actual.getUser().isPresent(), is(false));
        assertThat(actual.getPayload().isPresent(), is(false));
    }
}
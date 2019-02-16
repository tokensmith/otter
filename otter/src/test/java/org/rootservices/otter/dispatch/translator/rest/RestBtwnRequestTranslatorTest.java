package org.rootservices.otter.dispatch.translator.rest;

import helper.FixtureFactory;
import helper.entity.DummyPayload;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestBtwnRequest;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class RestBtwnRequestTranslatorTest {
    private RestBtwnRequestTranslator<DummyUser, DummyPayload> subject;

    @Before
    public void setUp() {
        subject = new RestBtwnRequestTranslator<DummyUser, DummyPayload>();
    }

    @Test
    public void toWhenFromIsAsk() {
        Ask from = FixtureFactory.makeAsk();

        RestBtwnRequest<DummyUser> actual = subject.to(from);

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
        assertThat(actual.getBody().isPresent(), is(false));
    }

    @Test
    public void toWhenFromIsRestRequest() {
        RestRequest<DummyUser, DummyPayload> from = FixtureFactory.makeRestRequest();

        RestBtwnRequest<DummyUser> actual = subject.to(from);

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
        assertThat(actual.getBody().isPresent(), is(false));
    }
}
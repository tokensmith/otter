package org.rootservices.otter.dispatch.translator;

import helper.FixtureFactory;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.dispatch.entity.RestBtwnRequest;
import org.rootservices.otter.dispatch.translator.rest.RestRequestTranslator;
import org.rootservices.otter.router.entity.io.Ask;

import java.util.Optional;

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
    public void toWhenFromIsAsk() {
        Ask from = FixtureFactory.makeAsk();

        RestRequest<DummyUser, DummyPayload> actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMatcher(), is(from.getMatcher()));
        assertThat(actual.getPossibleContentTypes(), is(from.getPossibleContentTypes()));
        assertThat(actual.getPossibleAccepts(), is(from.getPossibleAccepts()));
        assertThat(actual.getMethod(), is(from.getMethod()));
        assertThat(actual.getPathWithParams(), is(from.getPathWithParams()));
        assertThat(actual.getContentType(), is(from.getContentType()));
        assertThat(actual.getAccept(), is(from.getAccept()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getQueryParams(), is(from.getQueryParams()));
        assertThat(actual.getFormData(), is(from.getFormData()));
        assertThat(actual.getBody(), is(from.getBody()));
        assertThat(actual.getIpAddress(), is(from.getIpAddress()));
        assertThat(actual.getUser().isPresent(), is(false));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getCause().isPresent(), is(false));
    }

    @Test
    public void toWhenFromIsBtwnRequest() {
        RestBtwnRequest<DummyUser> from = FixtureFactory.makeRestBtwnRequest();
        Optional<DummyUser> user = Optional.of(new DummyUser());
        from.setUser(user);

        Optional<DummyPayload> payload = Optional.of(new DummyPayload());
        RestRequest<DummyUser, DummyPayload> actual = subject.to(from, payload);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMatcher(), is(from.getMatcher()));
        assertThat(actual.getPossibleContentTypes(), is(from.getPossibleContentTypes()));
        assertThat(actual.getPossibleAccepts(), is(from.getPossibleAccepts()));
        assertThat(actual.getMethod(), is(from.getMethod()));
        assertThat(actual.getPathWithParams(), is(from.getPathWithParams()));
        assertThat(actual.getContentType(), is(from.getContentType()));
        assertThat(actual.getAccept(), is(from.getAccept()));
        assertThat(actual.getHeaders(), is(from.getHeaders()));
        assertThat(actual.getCookies(), is(from.getCookies()));
        assertThat(actual.getQueryParams(), is(from.getQueryParams()));
        assertThat(actual.getFormData(), is(from.getFormData()));
        assertThat(actual.getBody(), is(from.getBody()));
        assertThat(actual.getIpAddress(), is(from.getIpAddress()));
        assertThat(actual.getUser().isPresent(), is(true));
        assertThat(actual.getUser().get(), is(user.get()));
        assertThat(actual.getPayload().isPresent(), is(true));
        assertThat(actual.getPayload().get(), is(payload.get()));
        assertThat(actual.getCause().isPresent(), is(false));
    }
}
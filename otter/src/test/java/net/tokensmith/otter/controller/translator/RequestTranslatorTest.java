package net.tokensmith.otter.controller.translator;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.dispatch.translator.RequestTranslator;
import net.tokensmith.otter.router.entity.io.Ask;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

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

        assertThat(actual.getCause().isPresent(), is(false));
        assertThat(actual.getMatcher(), is(from.getMatcher()));
        assertThat(actual.getPossibleContentTypes(), is(from.getPossibleContentTypes()));
        assertThat(actual.getPossibleAccepts(), is(from.getPossibleAccepts()));
        assertThat(actual.getMethod(), is(from.getMethod()));
        assertThat(actual.getScheme(), is(from.getScheme()));
        assertThat(actual.getAuthority(), is(from.getAuthority()));
        assertThat(actual.getPort(), is(from.getPort()));
        assertThat(actual.getPathWithParams(), is(from.getPathWithParams()));
        assertThat(actual.getContentType(), is(from.getContentType()));
        assertThat(actual.getAccept(), is(from.getAccept()));
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

    @Test
    public void toWithCause() {
        Ask from = FixtureFactory.makeAsk();
        Throwable cause = new RuntimeException();

        Request<DummySession, DummyUser> actual = subject.to(from, cause);

        assertThat(actual, is(notNullValue()));

        assertTrue(actual.getCause().isPresent());
        assertThat(actual.getCause().get(), is(cause));
        assertThat(actual.getMatcher(), is(from.getMatcher()));
        assertThat(actual.getPossibleContentTypes(), is(from.getPossibleContentTypes()));
        assertThat(actual.getPossibleAccepts(), is(from.getPossibleAccepts()));
        assertThat(actual.getMethod(), is(from.getMethod()));
        assertThat(actual.getScheme(), is(from.getScheme()));
        assertThat(actual.getAuthority(), is(from.getAuthority()));
        assertThat(actual.getPort(), is(from.getPort()));
        assertThat(actual.getPathWithParams(), is(from.getPathWithParams()));
        assertThat(actual.getContentType(), is(from.getContentType()));
        assertThat(actual.getAccept(), is(from.getAccept()));
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
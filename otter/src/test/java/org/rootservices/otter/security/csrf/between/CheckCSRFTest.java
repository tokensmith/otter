package org.rootservices.otter.security.csrf.between;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.CsrfException;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class CheckCSRFTest {
    private static String COOKIE_NAME = "CSRF";
    private static String FORM_FIELD_NAME = "CSRF";
    @Mock
    private DoubleSubmitCSRF mockDoubleSubmitCSRF;
    private CheckCSRF subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new CheckCSRF(COOKIE_NAME, FORM_FIELD_NAME, mockDoubleSubmitCSRF);
    }

    @Test
    public void processShouldBeOK() throws Exception {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        String challengeToken = "challenge-token";
        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        request.getCookies().put(COOKIE_NAME, cookie);
        request.getFormData().put(FORM_FIELD_NAME, Arrays.asList(challengeToken));

        when(mockDoubleSubmitCSRF.doTokensMatch(cookie.getValue(), challengeToken)).thenReturn(true);

        subject.process(Method.POST, request, response);

        assertThat(request.getCsrfChallenge().isPresent(), is(true));
        assertThat(request.getCsrfChallenge().get(), is(challengeToken));
    }

    @Test
    public void processWhenDontMatchShouldReturnFalse() throws Exception {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        request.getCookies().put(COOKIE_NAME, cookie);
        request.getFormData().put(FORM_FIELD_NAME, Arrays.asList("challenge-token"));

        when(mockDoubleSubmitCSRF.doTokensMatch(cookie.getValue(), "challenge-token")).thenReturn(false);

        HaltException actual = null;
        try {
            subject.process(Method.POST, request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(response.getStatusCode(), is(StatusCode.FORBIDDEN));

        assertThat(response.getStatusCode(), is(StatusCode.FORBIDDEN));
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(CsrfException.class));

    }

    @Test
    public void processWhenFormValueIsNullReturnFalse() throws Exception {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        request.getCookies().put(COOKIE_NAME, cookie);
        request.getFormData().put(FORM_FIELD_NAME, null);

        HaltException actual = null;
        try {
            subject.process(Method.POST, request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(response.getStatusCode(), is(StatusCode.FORBIDDEN));
        verify(mockDoubleSubmitCSRF, never()).doTokensMatch(anyString(), anyString());

        assertThat(response.getStatusCode(), is(StatusCode.FORBIDDEN));
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(CsrfException.class));
    }

    @Test
    public void processWhenCookieIsMissingShouldReturnFalse() throws Exception {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        HaltException actual = null;
        try {
            subject.process(Method.POST, request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(response.getStatusCode(), is(StatusCode.FORBIDDEN));
        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(CsrfException.class));

        verify(mockDoubleSubmitCSRF, never()).doTokensMatch(anyString(), anyString());
    }
}
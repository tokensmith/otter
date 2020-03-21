package net.tokensmith.otter.security.csrf.between.rest;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.exception.CsrfException;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.csrf.DoubleSubmitCSRF;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RestCheckCSRFTest {
    private static String COOKIE_NAME = "CSRF";
    private static String HDR_NAME = "X-CSRF";
    @Mock
    private DoubleSubmitCSRF mockDoubleSubmitCSRF;
    private RestCheckCSRF<DummySession, DummyUser> subject;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RestCheckCSRF<DummySession, DummyUser>(
            COOKIE_NAME, 
            HDR_NAME,
            mockDoubleSubmitCSRF,
            StatusCode.FORBIDDEN
        );
    }

    @Test
    public void processShouldBeOK() throws Exception {
        RestBtwnRequest<DummySession, DummyUser> request = FixtureFactory.makeRestBtwnRequest();
        RestBtwnResponse response = FixtureFactory.makeRestBtwnResponse();

        String challengeToken = "challenge-token";
        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        request.getCookies().put(COOKIE_NAME, cookie);
        request.getHeaders().put(HDR_NAME, challengeToken);

        when(mockDoubleSubmitCSRF.doTokensMatch(cookie.getValue(), challengeToken)).thenReturn(true);

        subject.process(Method.POST, request, response);

        // if it does not throw exception then its ok.
    }

    @Test
    public void processWhenDontMatchShouldThrowHalt() throws Exception {
        RestBtwnRequest<DummySession, DummyUser> request = FixtureFactory.makeRestBtwnRequest();
        RestBtwnResponse response = FixtureFactory.makeRestBtwnResponse();

        String challengeToken = "challenge-token";
        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        request.getCookies().put(COOKIE_NAME, cookie);
        request.getHeaders().put(HDR_NAME, challengeToken);

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
    public void processWhenFormValueIsNullShouldThrowHalt() throws Exception {
        RestBtwnRequest<DummySession, DummyUser> request = FixtureFactory.makeRestBtwnRequest();
        RestBtwnResponse response = FixtureFactory.makeRestBtwnResponse();

        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        request.getCookies().put(COOKIE_NAME, cookie);
        request.getFormData().put(HDR_NAME, null);

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
    public void processWhenCookieIsMissingShouldThrowHalt() throws Exception {
        RestBtwnRequest<DummySession, DummyUser> request = FixtureFactory.makeRestBtwnRequest();
        RestBtwnResponse response = FixtureFactory.makeRestBtwnResponse();

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
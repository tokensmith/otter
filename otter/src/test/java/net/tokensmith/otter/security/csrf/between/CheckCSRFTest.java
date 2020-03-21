package net.tokensmith.otter.security.csrf.between;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.security.csrf.between.html.CheckCSRF;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.exception.CsrfException;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.csrf.DoubleSubmitCSRF;

import java.util.Arrays;
import java.util.Optional;

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
    private CheckCSRF<DummySession, DummyUser> subject;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new CheckCSRF<DummySession, DummyUser>(COOKIE_NAME, FORM_FIELD_NAME, mockDoubleSubmitCSRF, StatusCode.FORBIDDEN, Optional.empty());
    }

    @Test
    public void processShouldBeOK() throws Exception {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

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
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

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
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

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
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

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
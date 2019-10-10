package org.rootservices.otter.security.csrf.between;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.csrf.CsrfClaims;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.security.csrf.exception.CsrfException;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;



public class PrepareCSRFTest {
    private static String COOKIE_NAME = "CSRF";
    @Mock
    private DoubleSubmitCSRF mockDoubleSubmitCSRF;
    private PrepareCSRF<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        CookieConfig cookieConfig = new CookieConfig(COOKIE_NAME, false, -1, true);
        subject = new PrepareCSRF<DummySession, DummyUser>(cookieConfig, mockDoubleSubmitCSRF);
    }

    @Test
    public void processShouldSetCookie() throws Exception {
        String challengeToken = "challenge-token";

        when(mockDoubleSubmitCSRF.makeChallengeToken()).thenReturn(challengeToken);
        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        when(mockDoubleSubmitCSRF.makeCsrfCookie(eq(COOKIE_NAME), any(), eq(false), eq(-1), eq(true))).thenReturn(cookie);

        ByteArrayOutputStream formValueJwt = new ByteArrayOutputStream();
        formValueJwt.write("formValueJwt".getBytes());

        when(mockDoubleSubmitCSRF.toJwt(any())).thenReturn(formValueJwt);
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        subject.process(Method.GET, request, response);

        assertThat(response.getCookies().get(COOKIE_NAME), is(notNullValue()));
        assertThat(response.getCookies().get(COOKIE_NAME), is(cookie));
        assertThat(request.getCsrfChallenge().isPresent(), is(true));
        assertThat(request.getCsrfChallenge().get(), is("formValueJwt"));

        verify(mockDoubleSubmitCSRF, times(3)).makeChallengeToken();
        verify(mockDoubleSubmitCSRF).makeCsrfCookie(eq(COOKIE_NAME), any(), eq(false), eq(-1), eq(true));
    }

    @Test
    public void processWhenCookieAlreadyThereShouldNotSetCookie() throws Exception {
        String challengeToken = "challenge-token";
        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();
        response.getCookies().put(COOKIE_NAME, cookie);

        // set up the csrf cookie value which is a jwt.
        CsrfClaims csrfClaims = new CsrfClaims();
        csrfClaims.setChallengeToken(challengeToken);
        JsonWebToken csrfJwt = new JsonWebToken();
        csrfJwt.setClaims(csrfClaims);

        when(mockDoubleSubmitCSRF.csrfToJwt(cookie.getValue())).thenReturn(csrfJwt);

        subject.process(Method.GET, request, response);

        assertThat(response.getCookies().get(COOKIE_NAME), is(notNullValue()));
        assertThat(response.getCookies().get(COOKIE_NAME), is(cookie));
        assertThat(request.getCsrfChallenge(), is(notNullValue()));
        assertThat(request.getCsrfChallenge().isPresent(), is(true));
        assertThat(request.getCsrfChallenge().get(), is(challengeToken));

        verify(mockDoubleSubmitCSRF, never()).makeChallengeToken();
        verify(mockDoubleSubmitCSRF, never()).makeCsrfCookie(eq(COOKIE_NAME), any(), eq(false), eq(-1), eq(true));
    }

    @Test
    public void processWhenCsrfExceptionShouldNotSetCookie() throws Exception {
        String challengeToken = "challenge-token";
        when(mockDoubleSubmitCSRF.makeChallengeToken()).thenReturn(challengeToken);

        CsrfException csrfException = new CsrfException("", null);
        when(mockDoubleSubmitCSRF.makeCsrfCookie(eq(COOKIE_NAME), any(), eq(false), eq(-1), eq(true))).thenThrow(csrfException);

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        subject.process(Method.GET, request, response);

        assertThat(response.getCookies().get(COOKIE_NAME), is(nullValue()));

        verify(mockDoubleSubmitCSRF, times(3)).makeChallengeToken();
        verify(mockDoubleSubmitCSRF).makeCsrfCookie(eq(COOKIE_NAME), any(), eq(false), eq(-1), eq(true));
    }
}
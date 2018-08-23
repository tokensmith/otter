package org.rootservices.otter.security.csrf.between;

import helper.FixtureFactory;
import helper.entity.DummySession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.csrf.CsrfClaims;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.security.csrf.exception.CsrfException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;



public class PrepareCSRFTest {
    private static String COOKIE_NAME = "CSRF";
    @Mock
    private DoubleSubmitCSRF mockDoubleSubmitCSRF;
    private PrepareCSRF<DummySession> subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        CookieConfig cookieConfig = new CookieConfig(COOKIE_NAME, false, -1);
        subject = new PrepareCSRF<DummySession>(cookieConfig, mockDoubleSubmitCSRF);
    }

    @Test
    public void processShouldSetCookie() throws Exception {
        String challengeToken = "challenge-token";
        when(mockDoubleSubmitCSRF.makeChallengeToken()).thenReturn(challengeToken);
        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        when(mockDoubleSubmitCSRF.makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1)).thenReturn(cookie);

        Request<DummySession> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        subject.process(Method.GET, request, response);

        assertThat(response.getCookies().get(COOKIE_NAME), is(notNullValue()));
        assertThat(response.getCookies().get(COOKIE_NAME), is(cookie));
        assertThat(request.getCsrfChallenge().isPresent(), is(true));
        assertThat(request.getCsrfChallenge().get(), is(challengeToken));

        verify(mockDoubleSubmitCSRF).makeChallengeToken();
        verify(mockDoubleSubmitCSRF).makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1);
    }

    @Test
    public void processWhenCookieAlreadyThereShouldNotSetCookie() throws Exception {
        String challengeToken = "challenge-token";
        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);

        Request<DummySession> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();
        response.getCookies().put(COOKIE_NAME, cookie);

        // set up the csrf cookie value which is a jwt.
        CsrfClaims csrfClaims = new CsrfClaims();
        csrfClaims.setChallengeToken(challengeToken);
        JsonWebToken csrfJwt = new JsonWebToken();
        csrfJwt.setClaims(csrfClaims);

        when(mockDoubleSubmitCSRF.csrfCookieValueToJwt(cookie.getValue())).thenReturn(csrfJwt);

        subject.process(Method.GET, request, response);

        assertThat(response.getCookies().get(COOKIE_NAME), is(notNullValue()));
        assertThat(response.getCookies().get(COOKIE_NAME), is(cookie));
        assertThat(request.getCsrfChallenge(), is(notNullValue()));
        assertThat(request.getCsrfChallenge().isPresent(), is(true));
        assertThat(request.getCsrfChallenge().get(), is(challengeToken));

        verify(mockDoubleSubmitCSRF, never()).makeChallengeToken();
        verify(mockDoubleSubmitCSRF, never()).makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1);
    }

    @Test
    public void processWhenCsrfExceptionShouldNotSetCookie() throws Exception {
        String challengeToken = "challenge-token";
        when(mockDoubleSubmitCSRF.makeChallengeToken()).thenReturn(challengeToken);

        CsrfException csrfException = new CsrfException("", null);
        when(mockDoubleSubmitCSRF.makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1)).thenThrow(csrfException);

        Request<DummySession> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        subject.process(Method.GET, request, response);

        assertThat(response.getCookies().get(COOKIE_NAME), is(nullValue()));

        verify(mockDoubleSubmitCSRF).makeChallengeToken();
        verify(mockDoubleSubmitCSRF).makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1);
    }
}
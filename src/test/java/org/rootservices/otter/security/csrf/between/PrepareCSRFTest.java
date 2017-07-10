package org.rootservices.otter.security.csrf.between;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.security.csrf.exception.CsrfException;
import suite.UnitTest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@Category(UnitTest.class)
public class PrepareCSRFTest {
    private static String COOKIE_NAME = "CSRF";
    @Mock
    private DoubleSubmitCSRF mockDoubleSubmitCSRF;
    private PrepareCSRF subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new PrepareCSRF(COOKIE_NAME, false, -1, mockDoubleSubmitCSRF);
    }

    @Test
    public void processShouldSetCookie() throws Exception {
        String challengeToken = "challenge-token";
        when(mockDoubleSubmitCSRF.makeChallengeToken()).thenReturn(challengeToken);
        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        when(mockDoubleSubmitCSRF.makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1)).thenReturn(cookie);

        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

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
        when(mockDoubleSubmitCSRF.makeChallengeToken()).thenReturn(challengeToken);
        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        when(mockDoubleSubmitCSRF.makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1)).thenReturn(cookie);

        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();
        response.getCookies().put(COOKIE_NAME, cookie);

        subject.process(Method.GET, request, response);

        assertThat(response.getCookies().get(COOKIE_NAME), is(notNullValue()));
        assertThat(response.getCookies().get(COOKIE_NAME), is(cookie));

        verify(mockDoubleSubmitCSRF, never()).makeChallengeToken();
        verify(mockDoubleSubmitCSRF, never()).makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1);
    }

    @Test
    public void processWhenCsrfExceptionShouldNotSetCookie() throws Exception {
        String challengeToken = "challenge-token";
        when(mockDoubleSubmitCSRF.makeChallengeToken()).thenReturn(challengeToken);

        CsrfException csrfException = new CsrfException("", null);
        when(mockDoubleSubmitCSRF.makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1)).thenThrow(csrfException);

        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        subject.process(Method.GET, request, response);

        assertThat(response.getCookies().get(COOKIE_NAME), is(nullValue()));

        verify(mockDoubleSubmitCSRF).makeChallengeToken();
        verify(mockDoubleSubmitCSRF).makeCsrfCookie(COOKIE_NAME, challengeToken, false, -1);
    }
}
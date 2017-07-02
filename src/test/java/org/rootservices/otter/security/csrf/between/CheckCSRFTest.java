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
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import suite.UnitTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@Category(UnitTest.class)
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
    public void processShouldBeOK() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        request.getCookies().put(COOKIE_NAME, cookie);
        request.getFormData().put(FORM_FIELD_NAME, "challenge-token");

        when(mockDoubleSubmitCSRF.doTokensMatch(cookie.getValue(), "challenge-token")).thenReturn(true);

        Boolean actual = subject.process(Method.POST, request, response);

        assertThat(actual, is(true));
    }

    @Test
    public void processWhenDontMatchShouldReturnFalse() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        request.getCookies().put(COOKIE_NAME, cookie);
        request.getFormData().put(FORM_FIELD_NAME, "challenge-token");

        when(mockDoubleSubmitCSRF.doTokensMatch(cookie.getValue(), "challenge-token")).thenReturn(false);

        Boolean actual = subject.process(Method.POST, request, response);

        assertThat(actual, is(false));
        assertThat(response.getStatusCode(), is(StatusCode.FORBIDDEN));
    }

    @Test
    public void processWhenFormValueIsNullReturnFalse() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Cookie cookie = FixtureFactory.makeCookie(COOKIE_NAME);
        request.getCookies().put(COOKIE_NAME, cookie);
        request.getFormData().put(FORM_FIELD_NAME, null);

        Boolean actual = subject.process(Method.POST, request, response);

        assertThat(actual, is(false));
        assertThat(response.getStatusCode(), is(StatusCode.FORBIDDEN));
    }

    @Test
    public void processWhenCookieIsMissingShouldReturnFalse() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Boolean actual = subject.process(Method.POST, request, response);

        assertThat(actual, is(false));

        verify(mockDoubleSubmitCSRF, never()).doTokensMatch(anyString(), anyString());
    }
}
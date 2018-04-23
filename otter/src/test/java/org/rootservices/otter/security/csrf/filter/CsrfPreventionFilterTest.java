package org.rootservices.otter.security.csrf.filter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.security.csrf.Csrf;
import org.rootservices.otter.security.csrf.exception.CsrfException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;


public class CsrfPreventionFilterTest {

    @Mock
    private Csrf mockCsrf;
    private CsrfPreventionFilter subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new CsrfPreventionFilter();
        subject.setCsrf(mockCsrf);
    }

    @Test
    public void doFilterShouldBeOk() throws Exception {
        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
        FilterChain mockFilterChain = mock(FilterChain.class);

        subject.doFilter(mockServletRequest, mockHttpServletResponse, mockFilterChain);

        verify(mockFilterChain, times(1)).doFilter(mockServletRequest, mockHttpServletResponse);
        verify(mockHttpServletResponse, times(0)).sendError(anyInt());
    }

    @Test
    public void doFilterShouldSendError() throws Exception {
        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
        FilterChain mockFilterChain = mock(FilterChain.class);

        doThrow(new CsrfException("")).when(mockCsrf).checkTokens(mockServletRequest);

        subject.doFilter(mockServletRequest, mockHttpServletResponse, mockFilterChain);

        verify(mockFilterChain, times(0)).doFilter(mockServletRequest, mockHttpServletResponse);
        verify(mockHttpServletResponse, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

}
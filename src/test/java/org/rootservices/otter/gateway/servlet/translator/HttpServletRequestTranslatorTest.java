package org.rootservices.otter.gateway.servlet.translator;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.router.entity.Method;
import suite.UnitTest;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Category(UnitTest.class)
public class HttpServletRequestTranslatorTest {
    @Mock
    private HttpServletRequestCookieTranslator mockHttpServletCookieTranslator;
    @Mock
    private HttpServletRequestHeaderTranslator mockHttpServletRequestHeaderTranslator;
    @Mock
    private QueryStringToMap mockQueryStringToMap;

    private HttpServletRequestTranslator subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new HttpServletRequestTranslator(
                mockHttpServletCookieTranslator,
                mockHttpServletRequestHeaderTranslator,
                mockQueryStringToMap
        );
    }

    @Test
    public void fromWhenCookiesAreNullShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("GET");
        when(mockContainerRequest.getRequestURI()).thenReturn("/foo");
        when(mockContainerRequest.getQueryString()).thenReturn("bar=bar-value");
        when(mockContainerRequest.getCookies()).thenReturn(null);
        when(mockHttpServletRequestHeaderTranslator.from(mockContainerRequest))
            .thenReturn(new HashMap<>());

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("bar", new ArrayList<>());
        queryParams.get("bar").add("bar-value");

        when(mockQueryStringToMap.run(Optional.of("bar=bar-value")))
            .thenReturn(queryParams);

        Request actual = subject.from(mockContainerRequest);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.GET));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        // TODO: body
        assertThat(actual.getQueryParams(), is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getCookies().size(), is(0));
        assertThat(actual.getPathWithParams(), is("/foo?bar=bar-value"));
        assertThat(actual.getMatcher(), is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
        assertThat(actual.getFormData(), is(notNullValue()));
        assertThat(actual.getFormData().size(), is(0));
    }

    @Test
    public void fromWhenPostAndCookiesAreNullShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("POST");
        when(mockContainerRequest.getRequestURI()).thenReturn("/foo");
        when(mockContainerRequest.getQueryString()).thenReturn("bar=bar-value");
        when(mockContainerRequest.getCookies()).thenReturn(null);
        when(mockHttpServletRequestHeaderTranslator.from(mockContainerRequest))
                .thenReturn(new HashMap<>());

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("bar", new ArrayList<>());
        queryParams.get("bar").add("bar-value");

        when(mockQueryStringToMap.run(Optional.of("bar=bar-value")))
                .thenReturn(queryParams);

        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put("bar", new String[]{"bar-value"});
        parameterMap.put("form-field", new String[]{"form-value"});
        when(mockContainerRequest.getParameterMap()).thenReturn(parameterMap);

        Request actual = subject.from(mockContainerRequest);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.POST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        // TODO: body
        assertThat(actual.getQueryParams(), is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getCookies().size(), is(0));
        assertThat(actual.getPathWithParams(), is("/foo?bar=bar-value"));
        assertThat(actual.getMatcher(), is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
        assertThat(actual.getFormData(), is(notNullValue()));
        assertThat(actual.getFormData().size(), is(1));
        assertThat(actual.getFormData().get("form-field"), is("form-value"));
    }


}
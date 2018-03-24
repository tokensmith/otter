package org.rootservices.otter.gateway.servlet.translator;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import suite.UnitTest;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Category(UnitTest.class)
public class HttpServletRequestHeaderTranslatorTest {
    private HttpServletRequestHeaderTranslator subject;

    @Before
    public void setUp() {
        subject = new HttpServletRequestHeaderTranslator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fromWhenNoHeadersShouldBeEmptyMap() {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        Enumeration containerHeaders = Collections.enumeration(Collections.emptyList());
        when(mockContainerRequest.getHeaderNames()).thenReturn(containerHeaders);

        Map<String, String> actual = subject.from(mockContainerRequest);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }

    @Test
    public void fromShouldTranslateOk() {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);

        List<String> headerNames = new ArrayList<>();
        headerNames.add("header-name-1");
        headerNames.add("header-name-2");
        Enumeration<String> containerHeaders = Collections.enumeration(headerNames);

        when(mockContainerRequest.getHeaderNames()).thenReturn(containerHeaders);

        when(mockContainerRequest.getHeader("header-name-1")).thenReturn("header-value-1");
        when(mockContainerRequest.getHeader("header-name-2")).thenReturn("header-value-2");

        Map<String, String> actual = subject.from(mockContainerRequest);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(2));
        assertThat(actual.get("header-name-1"), is("header-value-1"));
        assertThat(actual.get("header-name-2"), is("header-value-2"));

    }

}
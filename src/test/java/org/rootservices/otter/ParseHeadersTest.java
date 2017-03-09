package org.rootservices.otter;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.jetty.server.Request;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 1/8/17.
 */
public class ParseHeadersTest {
    private ParseHeaders subject;

    @Before
    public void setUp() {
        this.subject = new ParseHeaders();
    }

    @Test
    public void parseAllHeadersShouldBeOk() throws Exception {
        Request mockRequest = mock(Request.class);
        List<String> headers = new ArrayList<>();
        headers.add("Accept");
        headers.add("Accept-Encoding");

        Enumeration<String> headerNames = Collections.enumeration(headers);
        when(mockRequest.getHeaderNames()).thenReturn(headerNames);

        when(mockRequest.getHeader("Accept")).thenReturn("application/json");
        when(mockRequest.getHeader("Accept-Encoding")).thenReturn("gzip, deflate");

    }



}
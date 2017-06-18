package org.rootservices.otter.gateway.servlet;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class ServletGatewayTest {
    @Mock
    private HttpServletRequestTranslator mockHttpServletRequestTranslator;
    @Mock
    private HttpServletRequestMerger mockHttpServletRequestMerger;
    @Mock
    private HttpServletResponseMerger mockHttpServletResponseMerger;
    @Mock
    private Engine mockEngine;
    @Mock
    private Between mockPrepareCSRF;
    @Mock
    private Between mockCheckCSRF;

    private ServletGateway subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ServletGateway(
                mockHttpServletRequestTranslator,
                mockHttpServletRequestMerger,
                mockHttpServletResponseMerger,
                mockEngine,
                mockPrepareCSRF,
                mockCheckCSRF
        );
    }

    @Test
    public void processRequestResourceFoundShouldBeOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);

        Request request = new Request();

        when(mockHttpServletRequestTranslator.from(mockContainerRequest))
                .thenReturn(request);
        Optional<Response> resourceResponse = Optional.of(FixtureFactory.makeResponse());
        when(mockEngine.route(eq(request), any(Response.class))).thenReturn(resourceResponse);

        subject.processRequest(mockContainerRequest, mockContainerResponse);

        // should never call the not found resource.
        verify(mockEngine, never()).executeResourceMethod(any(Route.class), any(Request.class), any(Response.class));

        verify(mockHttpServletResponseMerger).merge(mockContainerResponse, null, resourceResponse.get());
        verify(mockHttpServletRequestMerger).merge(mockContainerRequest, mockContainerResponse, resourceResponse.get());
    }

    @Test
    public void processRequestResourceNotFoundShouldExecuteNotFound() throws Exception {
        Route notFoundRoute = FixtureFactory.makeRoute("");
        subject.setNotFoundRoute(notFoundRoute);

        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);

        Request request = new Request();

        when(mockHttpServletRequestTranslator.from(mockContainerRequest))
                .thenReturn(request);

        // original engine call does NOT return a response.
        when(mockEngine.route(eq(request), any(Response.class))).thenReturn(Optional.empty());

        Response resourceResponse = FixtureFactory.makeResponse();
        when(mockEngine.executeResourceMethod(
                eq(notFoundRoute),
                eq(request),
                any(Response.class)
        )).thenReturn(resourceResponse);

        subject.processRequest(mockContainerRequest, mockContainerResponse);

        // should call the not found resource.
        verify(mockEngine).executeResourceMethod(
                eq(notFoundRoute),
                eq(request),
                any(Response.class)
        );

        verify(mockHttpServletResponseMerger).merge(mockContainerResponse, null, resourceResponse);
        verify(mockHttpServletRequestMerger).merge(mockContainerRequest, mockContainerResponse, resourceResponse);

    }

}
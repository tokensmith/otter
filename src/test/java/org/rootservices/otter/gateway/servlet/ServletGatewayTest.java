package org.rootservices.otter.gateway.servlet;

import helper.FixtureFactory;
import helper.entity.FakeResource;
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

    private ServletGateway subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ServletGateway(
                mockHttpServletRequestTranslator,
                mockHttpServletRequestMerger,
                mockHttpServletResponseMerger,
                mockEngine
        );
    }

    @Test
    public void processRequestResourceFoundShouldBeOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);

        Request request = new Request();
        when(mockHttpServletRequestTranslator.from(mockContainerRequest))
                .thenReturn(request);
        Optional<Response> response = Optional.of(FixtureFactory.makeResponse());
        when(mockEngine.route(request)).thenReturn(response);

        subject.processRequest(mockContainerRequest, mockContainerResponse);

        // should never call the not found resource.
        verify(mockEngine, never()).executeResourceMethod(any(Resource.class), any(Request.class));

        verify(mockHttpServletResponseMerger).merge(mockContainerResponse, null, response.get());
        verify(mockHttpServletRequestMerger).merge(mockContainerRequest, mockContainerResponse, response.get());
    }

    @Test
    public void processRequestResourceNotFoundShouldExecuteNotFound() throws Exception {
        FakeResource notFoundResource = new FakeResource();
        subject.setNotFoundResource(notFoundResource);

        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);

        Request request = new Request();
        when(mockHttpServletRequestTranslator.from(mockContainerRequest))
                .thenReturn(request);

        // original engine call does NOT return a response.
        when(mockEngine.route(request)).thenReturn(Optional.empty());

        Response response = FixtureFactory.makeResponse();
        when(mockEngine.executeResourceMethod(notFoundResource, request))
                .thenReturn(response);

        subject.processRequest(mockContainerRequest, mockContainerResponse);

        // should call the not found resource.
        verify(mockEngine).executeResourceMethod(notFoundResource, request);

        verify(mockHttpServletResponseMerger).merge(mockContainerResponse, null, response);
        verify(mockHttpServletRequestMerger).merge(mockContainerRequest, mockContainerResponse, response);

    }

}
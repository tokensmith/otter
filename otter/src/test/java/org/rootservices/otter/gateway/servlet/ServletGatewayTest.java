package org.rootservices.otter.gateway.servlet;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.router.Dispatcher;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.entity.Route;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class ServletGatewayTest {
    @Mock
    private HttpServletRequestTranslator<DummySession, DummyUser> mockHttpServletRequestTranslator;
    @Mock
    private HttpServletRequestMerger mockHttpServletRequestMerger;
    @Mock
    private HttpServletResponseMerger<DummySession> mockHttpServletResponseMerger;
    @Mock
    private Engine<DummySession, DummyUser> mockEngine;
    @Mock
    private Dispatcher<DummySession, DummyUser> mockDispatcher;
    @Mock
    private LocationTranslator<DummySession, DummyUser> mockLocationTranslator;

    private ServletGateway<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockEngine.getDispatcher()).thenReturn(mockDispatcher);
        subject = new ServletGateway<DummySession, DummyUser>(
                mockHttpServletRequestTranslator,
                mockHttpServletRequestMerger,
                mockHttpServletResponseMerger,
                mockEngine,
                mockLocationTranslator
        );
    }

    @Test
    public void processRequestResourceFoundShouldBeOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        byte[] containerBody = null;

        Request<DummySession, DummyUser> request = new Request<DummySession, DummyUser>();

        when(mockHttpServletRequestTranslator.from(mockContainerRequest, containerBody))
                .thenReturn(request);

        Response<DummySession> resourceResponse = FixtureFactory.makeResponse();

        when(mockEngine.route(eq(request), any())).thenReturn(resourceResponse);

        subject.processRequest(mockContainerRequest, mockContainerResponse, containerBody);

        // should never call the not found resource.
        verify(mockEngine, never()).executeResourceMethod(any(), any(), any());

        verify(mockHttpServletResponseMerger).merge(mockContainerResponse, null, resourceResponse);
        verify(mockHttpServletRequestMerger).merge(mockContainerRequest, resourceResponse);
    }

    @Test
    public void setErrorRouteShouldAssign() {
        Route<DummySession, DummyUser> notFoundRoute = FixtureFactory.makeRoute();

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = new HashMap<StatusCode, Route<DummySession, DummyUser>>();
        when(mockEngine.getErrorRoutes()).thenReturn(errorRoutes);

        subject.setErrorRoute(StatusCode.NOT_FOUND, notFoundRoute);

        assertThat(errorRoutes.size(), is(1));
        assertThat(errorRoutes.get(StatusCode.NOT_FOUND), is(notFoundRoute));
    }

    @Test
    public void processRequestWhenExceptionShouldReturnServerError() throws Exception {
        Route<DummySession, DummyUser> notFoundRoute = FixtureFactory.makeRoute();
        subject.setErrorRoute(StatusCode.NOT_FOUND, notFoundRoute);

        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        byte[] containerBody = null;

        doThrow(new RuntimeException()).when(mockHttpServletRequestTranslator).from(mockContainerRequest, containerBody);

        GatewayResponse actual = subject.processRequest(mockContainerRequest, mockContainerResponse, containerBody);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPayload(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate(), is(notNullValue()));
        assertThat(actual.getTemplate().isPresent(), is(false));

        verify(mockContainerResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void processRequestWhenIOExceptionShouldReturnServerError() throws Exception {
        Route<DummySession, DummyUser> notFoundRoute = FixtureFactory.makeRoute();
        subject.setErrorRoute(StatusCode.NOT_FOUND, notFoundRoute);

        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        byte[] containerBody = null;

        doThrow(new IOException()).when(mockHttpServletRequestTranslator).from(mockContainerRequest, containerBody);

        GatewayResponse actual = subject.processRequest(mockContainerRequest, mockContainerResponse, containerBody);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPayload(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate(), is(notNullValue()));
        assertThat(actual.getTemplate().isPresent(), is(false));

        verify(mockContainerResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
package org.rootservices.otter.gateway.servlet;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.router.Dispatcher;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.translatable.Translatable;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
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
    private Dispatcher mockDispatcher;

    private ServletGateway subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockEngine.getDispatcher()).thenReturn(mockDispatcher);

        Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser, ? extends Translatable>> locationTranslators;
        locationTranslators = new HashMap<>();

        subject = new ServletGateway(
                mockHttpServletRequestTranslator,
                mockHttpServletRequestMerger,
                mockHttpServletResponseMerger,
                mockEngine,
                locationTranslators,
                OtterAppFactory.WRITE_CHUNK_SIZE
        );
    }

    @Test
    public void processRequestResourceFoundShouldBeOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        byte[] containerBody = null;

        Ask ask = new Ask();

        when(mockHttpServletRequestTranslator.from(mockContainerRequest, containerBody))
                .thenReturn(ask);

        Answer resourceAnswer = FixtureFactory.makeAnswer();

        when(mockEngine.route(eq(ask), any())).thenReturn(resourceAnswer);

        subject.processRequest(mockContainerRequest, mockContainerResponse, containerBody);

        verify(mockHttpServletResponseMerger).merge(mockContainerResponse, null, resourceAnswer);
        verify(mockHttpServletRequestMerger).merge(mockContainerRequest, resourceAnswer);
    }

    @Test
    public void setErrorRouteShouldAssign() {
        Route<DummySession, DummyUser> notFoundRoute = FixtureFactory.makeRoute();

        Map<StatusCode, RouteRunner> errorRoutes = new HashMap<>();
        when(mockEngine.getErrorRoutes()).thenReturn(errorRoutes);

        subject.setErrorRoute(StatusCode.NOT_FOUND, notFoundRoute);

        assertThat(errorRoutes.size(), is(1));
        assertThat(errorRoutes.get(StatusCode.NOT_FOUND), is(notNullValue()));
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
        assertThat(actual.getWriteChunkSize(), is(OtterAppFactory.WRITE_CHUNK_SIZE));

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
        assertThat(actual.getWriteChunkSize(), is(OtterAppFactory.WRITE_CHUNK_SIZE));

        verify(mockContainerResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
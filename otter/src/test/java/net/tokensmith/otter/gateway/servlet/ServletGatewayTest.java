package net.tokensmith.otter.gateway.servlet;

import helper.FixtureFactory;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.gateway.servlet.merger.HttpServletRequestMerger;
import net.tokensmith.otter.gateway.servlet.merger.HttpServletResponseMerger;
import net.tokensmith.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import net.tokensmith.otter.gateway.translator.LocationTranslator;
import net.tokensmith.otter.gateway.translator.RestLocationTranslator;
import net.tokensmith.otter.router.Dispatcher;
import net.tokensmith.otter.router.Engine;
import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.router.entity.io.Ask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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

        Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser>> locationTranslators;
        locationTranslators = new HashMap<>();

        Map<String, RestLocationTranslator<? extends DefaultSession, ? extends DefaultUser, ?>> restLocationTranslators = new HashMap<>();

        subject = new ServletGateway(
                mockHttpServletRequestTranslator,
                mockHttpServletRequestMerger,
                mockHttpServletResponseMerger,
                mockEngine,
                locationTranslators,
                restLocationTranslators,
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
    public void processRequestWhenExceptionShouldReturnServerError() throws Exception {
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
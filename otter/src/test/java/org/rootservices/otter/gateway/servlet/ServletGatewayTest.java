package org.rootservices.otter.gateway.servlet;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.FakeResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.router.Dispatcher;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
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
    private Between<DummySession, DummyUser> mockPrepareCSRF;
    @Mock
    private Between<DummySession, DummyUser> mockCheckCSRF;

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
                mockPrepareCSRF,
                mockCheckCSRF
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

    @Test
    public void getShouldAddRouteWithEmptyBeforeAfter() {

        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.GET)).thenReturn(locations);

        FakeResource resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.get("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void getCsrfProtectShouldAddRouteWithCsrfBeforeEmptyAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.GET)).thenReturn(locations);

        FakeResource resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.getCsrfProtect("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(1));
        assertThat(actual.getRoute().getBefore().get(0), is(mockPrepareCSRF));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));

    }

    @Test
    public void postShouldAddRouteWithEmptyBeforeAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.POST)).thenReturn(locations);

        FakeResource resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.post("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void postCsrfProtectShouldAddRouteWithCsrfBeforeEmptyAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.POST)).thenReturn(locations);

        FakeResource resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.postCsrfProtect("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(1));
        assertThat(actual.getRoute().getBefore().get(0), is(mockCheckCSRF));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void putShouldAddRouteWithEmptyBeforeAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.PUT)).thenReturn(locations);

        Resource<DummySession, DummyUser> resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.put("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void patchShouldAddRouteWithEmptyBeforeAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.PATCH)).thenReturn(locations);

        Resource<DummySession, DummyUser> resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.patch("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void deleteShouldAddRouteWithEmptyBeforeAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.DELETE)).thenReturn(locations);

        Resource<DummySession, DummyUser> resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.delete("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void connectShouldAddRouteWithEmptyBeforeAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.CONNECT)).thenReturn(locations);

        FakeResource resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.connect("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void optionsShouldAddRouteWithEmptyBeforeAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.OPTIONS)).thenReturn(locations);

        FakeResource resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.options("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void traceShouldAddRouteWithEmptyBeforeAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.TRACE)).thenReturn(locations);

        FakeResource resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.trace("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void headShouldAddRouteWithEmptyBeforeAfter() {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        when(mockDispatcher.locations(Method.HEAD)).thenReturn(locations);

        FakeResource resource = new FakeResource();
        Location<DummySession, DummyUser> actual = subject.head("/path", resource);

        assertThat(locations, is(notNullValue()));
        assertThat(locations.size(), is(1));
        assertThat(locations.get(0), is(actual));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is("/path"));
        assertThat(actual.getRoute().getBefore(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter(), is(notNullValue()));
        assertThat(actual.getRoute().getAfter().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }
}
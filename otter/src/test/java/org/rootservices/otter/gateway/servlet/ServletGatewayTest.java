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
import org.rootservices.otter.router.Dispatcher;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.RouteBuilder;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.between.EncryptSession;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
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
    private Dispatcher mockDispatcher;
    @Mock
    private Between mockPrepareCSRF;
    @Mock
    private Between mockCheckCSRF;
    @Mock
    private EncryptSession mockEncryptSession;

    private ServletGateway subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockEngine.getDispatcher()).thenReturn(mockDispatcher);
        subject = new ServletGateway(
                mockHttpServletRequestTranslator,
                mockHttpServletRequestMerger,
                mockHttpServletResponseMerger,
                mockEngine,
                mockPrepareCSRF,
                mockCheckCSRF,
                mockEncryptSession
        );
    }

    @Test
    public void processRequestResourceFoundShouldBeOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        String body = null;

        Request request = new Request();

        when(mockHttpServletRequestTranslator.from(mockContainerRequest, body))
                .thenReturn(request);
        Optional<Response> resourceResponse = Optional.of(FixtureFactory.makeResponse());
        when(mockEngine.route(eq(request), any(Response.class))).thenReturn(resourceResponse);

        subject.processRequest(mockContainerRequest, mockContainerResponse, body);

        // should never call the not found resource.
        verify(mockEngine, never()).executeResourceMethod(any(Route.class), any(Request.class), any(Response.class));

        verify(mockHttpServletResponseMerger).merge(mockContainerResponse, null, resourceResponse.get());
        verify(mockHttpServletRequestMerger).merge(mockContainerRequest, resourceResponse.get());
    }

    @Test
    public void processRequestResourceNotFoundShouldExecuteNotFound() throws Exception {
        Route notFoundRoute = FixtureFactory.makeRoute("");
        subject.setNotFoundRoute(notFoundRoute);

        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        String body = null;

        Request request = new Request();

        when(mockHttpServletRequestTranslator.from(mockContainerRequest, body))
                .thenReturn(request);

        // original engine call does NOT return a response.
        when(mockEngine.route(eq(request), any(Response.class))).thenReturn(Optional.empty());

        Response resourceResponse = FixtureFactory.makeResponse();
        when(mockEngine.executeResourceMethod(
                eq(notFoundRoute),
                eq(request),
                any(Response.class)
        )).thenReturn(resourceResponse);

        subject.processRequest(mockContainerRequest, mockContainerResponse, body);

        // should call the not found resource.
        verify(mockEngine).executeResourceMethod(
                eq(notFoundRoute),
                eq(request),
                any(Response.class)
        );

        verify(mockHttpServletResponseMerger).merge(mockContainerResponse, null, resourceResponse);
        verify(mockHttpServletRequestMerger).merge(mockContainerRequest, resourceResponse);

    }

    @Test
    public void getShouldAddRouteWithEmptyBeforeAfter() {

        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getGet()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.get("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(0));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void getCsrfProtectShouldAddRouteWithCsrfBeforeEmptyAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getGet()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.getCsrfProtect("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(1));
        assertThat(routes.get(0).getBefore().get(0), is(mockPrepareCSRF));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));

    }

    @Test
    public void postShouldAddRouteWithEmptyBeforeAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getPost()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.post("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(0));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void postCsrfProtectShouldAddRouteWithCsrfBeforeEmptyAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getPost()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.postCsrfProtect("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(1));
        assertThat(routes.get(0).getBefore().get(0), is(mockCheckCSRF));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void putShouldAddRouteWithEmptyBeforeAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getPut()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.put("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(0));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void patchShouldAddRouteWithEmptyBeforeAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getPatch()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.patch("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(0));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void deleteShouldAddRouteWithEmptyBeforeAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getDelete()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.delete("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(0));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void connectShouldAddRouteWithEmptyBeforeAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getConnect()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.connect("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(0));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void optionsShouldAddRouteWithEmptyBeforeAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getOptions()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.options("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(0));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void traceShouldAddRouteWithEmptyBeforeAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getTrace()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.trace("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(0));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void headShouldAddRouteWithEmptyBeforeAfter() {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getHead()).thenReturn(routes);

        Resource resource = new FakeResource();
        subject.head("/path", resource);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0).getResource(), is(resource));
        assertThat(routes.get(0).getPattern(), is(notNullValue()));
        assertThat(routes.get(0).getPattern().pattern(), is("/path"));
        assertThat(routes.get(0).getBefore(), is(notNullValue()));
        assertThat(routes.get(0).getBefore().size(), is(0));
        assertThat(routes.get(0).getAfter(), is(notNullValue()));
        assertThat(routes.get(0).getAfter().size(), is(0));
    }

    @Test
    public void getRouteShouldAddRoute() throws Exception {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getGet()).thenReturn(routes);

        Route route = new RouteBuilder().build();
        subject.getRoute(route);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0), is(route));

    }

    @Test
    public void postRouteShouldAddRoute() throws Exception {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getPost()).thenReturn(routes);

        Route route = new RouteBuilder().build();
        subject.postRoute(route);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0), is(route));
    }

    @Test
    public void putRouteShouldAddRoute() throws Exception {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getPut()).thenReturn(routes);

        Route route = new RouteBuilder().build();
        subject.putRoute(route);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0), is(route));

    }

    @Test
    public void patchRouteShouldAddRoute() throws Exception {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getPatch()).thenReturn(routes);

        Route route = new RouteBuilder().build();
        subject.patchRoute(route);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0), is(route));

    }

    @Test
    public void deleteRouteShouldAddRoute() throws Exception {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getDelete()).thenReturn(routes);

        Route route = new RouteBuilder().build();
        subject.deleteRoute(route);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0), is(route));
    }

    @Test
    public void connectRouteShouldAddRoute() throws Exception {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getConnect()).thenReturn(routes);

        Route route = new RouteBuilder().build();
        subject.connectRoute(route);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0), is(route));
    }

    @Test
    public void optionsRouteShouldAddRoute() throws Exception {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getOptions()).thenReturn(routes);

        Route route = new RouteBuilder().build();
        subject.optionsRoute(route);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0), is(route));
    }

    @Test
    public void traceRouteShouldAddRoute() throws Exception {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getTrace()).thenReturn(routes);

        Route route = new RouteBuilder().build();
        subject.traceRoute(route);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0), is(route));
    }

    @Test
    public void headRouteShouldAddRoute() throws Exception {
        List<Route> routes = new ArrayList<>();
        when(mockDispatcher.getHead()).thenReturn(routes);

        Route route = new RouteBuilder().build();
        subject.headRoute(route);

        assertThat(routes, is(notNullValue()));
        assertThat(routes.size(), is(1));
        assertThat(routes.get(0), is(route));

    }
}
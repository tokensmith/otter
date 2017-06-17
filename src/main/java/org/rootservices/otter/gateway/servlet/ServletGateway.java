package org.rootservices.otter.gateway.servlet;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.RouteBuilder;
import org.rootservices.otter.router.entity.Route;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class ServletGateway {
    protected static Logger logger = LogManager.getLogger(ServletGateway.class);

    private HttpServletRequestTranslator httpServletRequestTranslator;
    private HttpServletRequestMerger httpServletRequestMerger;
    private HttpServletResponseMerger httpServletResponseMerger;
    private Engine engine;
    private Route notFoundRoute;

    public ServletGateway(HttpServletRequestTranslator httpServletRequestTranslator, HttpServletRequestMerger httpServletRequestMerger, HttpServletResponseMerger httpServletResponseMerger, Engine engine) {
        this.httpServletRequestTranslator = httpServletRequestTranslator;
        this.httpServletRequestMerger = httpServletRequestMerger;
        this.httpServletResponseMerger = httpServletResponseMerger;
        this.engine = engine;
    }

    public void processRequest(HttpServletRequest containerRequest, HttpServletResponse containerResponse) {
        try {
            Request request = httpServletRequestTranslator.from(containerRequest);
            Response response = new ResponseBuilder()
                    .headers(new HashMap<>())
                    .cookies(request.getCookies())
                    .payload(Optional.empty())
                    .presenter(Optional.empty())
                    .template(Optional.empty())
                    .build();

            Optional<Response> resourceResponse =  engine.route(request, response);

            if (!resourceResponse.isPresent()) {
                resourceResponse = Optional.of(engine.executeResourceMethod(notFoundRoute, request, response));
            }

            httpServletResponseMerger.merge(containerResponse, containerRequest.getCookies(), resourceResponse.get());
            httpServletRequestMerger.merge(containerRequest, containerResponse, resourceResponse.get());

        } catch (IOException | ServletException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void get(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getGet().add(route);
    }

    public void post(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getPost().add(route);
    }

    public void put(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getPut().add(route);
    }

    public void patch(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getPatch().add(route);
    }

    public void delete(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getDelete().add(route);
    }

    public void connect(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getConnect().add(route);
    }

    public void options(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getOptions().add(route);
    }

    public void trace(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getTrace().add(route);
    }

    public void head(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getHead().add(route);
    }

    public void setNotFoundRoute(Route notFoundRoute) {
        this.notFoundRoute = notFoundRoute;
    }

}

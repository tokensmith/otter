package org.rootservices.otter.gateway.servlet;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.request.HttpServletRequestTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.entity.Route;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ServletGateway {
    protected static Logger logger = LogManager.getLogger(ServletGateway.class);

    private HttpServletRequestTranslator httpServletRequestTranslator;
    private HttpServletRequestMerger httpServletRequestMerger;
    private HttpServletResponseMerger httpServletResponseMerger;
    private Engine engine;
    private Resource notFoundResource;

    public ServletGateway(HttpServletRequestTranslator httpServletRequestTranslator, HttpServletRequestMerger httpServletRequestMerger, HttpServletResponseMerger httpServletResponseMerger, Engine engine) {
        this.httpServletRequestTranslator = httpServletRequestTranslator;
        this.httpServletRequestMerger = httpServletRequestMerger;
        this.httpServletResponseMerger = httpServletResponseMerger;
        this.engine = engine;
    }

    public void processRequest(HttpServletRequest containerRequest, HttpServletResponse containerResponse) {
        try {
            Request request = httpServletRequestTranslator.from(containerRequest);
            Optional<Response> response =  engine.route(request);

            if (!response.isPresent()) {
                response = Optional.of(engine.executeResourceMethod(notFoundResource, request));
            }

            httpServletResponseMerger.merge(containerResponse, containerRequest.getCookies(), response.get());
            httpServletRequestMerger.merge(containerRequest, containerResponse, response.get());

        } catch (IOException | ServletException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public List<Route> getGet() {
        return engine.getDispatcher().getGet();
    }

    public List<Route> getPost() {
        return engine.getDispatcher().getPost();
    }

    public List<Route> getPut() {
        return engine.getDispatcher().getPut();
    }

    public List<Route> getPatch() {
        return engine.getDispatcher().getPatch();
    }

    public List<Route> getDelete() {
        return engine.getDispatcher().getDelete();
    }

    public List<Route> getConnect() {
        return engine.getDispatcher().getConnect();
    }

    public List<Route> getOptions() {
        return engine.getDispatcher().getOptions();
    }

    public List<Route> getTrace() {
        return engine.getDispatcher().getTrace();
    }

    public List<Route> getHead() {
        return engine.getDispatcher().getHead();
    }

    public void setNotFoundResource(Resource notFoundResource) {
        this.notFoundResource = notFoundResource;
    }
}

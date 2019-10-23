package net.tokensmith.otter.controller.error.html;


import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class NotAcceptableResource<S extends DefaultSession, U extends DefaultUser> extends Resource<S, U> {
    protected static Logger LOGGER = LoggerFactory.getLogger(NotAcceptableResource.class);
    private static Optional<String> JSP_PATH;

    public NotAcceptableResource(String template) {
        JSP_PATH = Optional.of(template);
    }

    protected StatusCode statusCode() {
        return StatusCode.NOT_ACCEPTABLE;
    }

    protected void log(Request<S, U> request) {
        LOGGER.info("content-type: {}, possible content-type: {}, accept: {}, possible accept: {}",
                request.getContentType(), request.getPossibleContentTypes(),
                request.getAccept(), request.getPossibleAccepts()
        );
    }

    @Override
    public Response<S> get(Request<S, U> request, Response<S> response) {
        log(request);
        response.setStatusCode(statusCode());
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<S> post(Request<S, U> request, Response<S> response) {
        log(request);
        response.setStatusCode(statusCode());
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<S> put(Request<S, U> request, Response<S> response) {
        log(request);
        response.setStatusCode(statusCode());
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<S> delete(Request<S, U> request, Response<S> response) {
        log(request);
        response.setStatusCode(statusCode());
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<S> connect(Request<S, U> request, Response<S> response) {
        log(request);
        response.setStatusCode(statusCode());
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<S> options(Request<S, U> request, Response<S> response) {
        log(request);
        response.setStatusCode(statusCode());
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<S> trace(Request<S, U> request, Response<S> response) {
        log(request);
        response.setStatusCode(statusCode());
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<S> patch(Request<S, U> request, Response<S> response) {
        log(request);
        response.setStatusCode(statusCode());
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<S> head(Request<S, U> request, Response<S> response) {
        log(request);
        response.setStatusCode(statusCode());
        response.setTemplate(JSP_PATH);
        return response;
    }
}

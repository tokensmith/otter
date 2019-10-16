package org.rootservices.otter.controller.error.html;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MediaTypeResource<S extends DefaultSession, U extends DefaultUser> extends Resource<S, U> {
    protected static Logger LOGGER = LoggerFactory.getLogger(MediaTypeResource.class);
    private static Optional<String> JSP_PATH ;

    public MediaTypeResource(String template) {
        JSP_PATH = Optional.of(template);
    }

    protected StatusCode statusCode() {
        return StatusCode.UNSUPPORTED_MEDIA_TYPE;
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

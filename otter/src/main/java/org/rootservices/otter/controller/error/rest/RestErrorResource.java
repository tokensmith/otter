package org.rootservices.otter.controller.error.rest;

import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;

import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.translatable.Translatable;

import java.util.Optional;

public abstract class RestErrorResource<U extends DefaultUser, P> extends RestResource<U, P> {

    public abstract Optional<P> to(Throwable from);
    public abstract StatusCode statusCode();

    public RestResponse<P> get(RestRequest<U, P> request, RestResponse<P> response) {
        Throwable cause = request.getCause().get();
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> post(RestRequest<U, P>  request, RestResponse<P> response) {
        Throwable cause = request.getCause().get();
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> put(RestRequest<U, P>  request, RestResponse<P> response) {
        Throwable cause = request.getCause().get();
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> delete(RestRequest<U, P>  request, RestResponse<P> response) {
        Throwable cause = request.getCause().get();
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> connect(RestRequest<U, P>  request, RestResponse<P> response) {
        Throwable cause = request.getCause().get();
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> options(RestRequest<U, P>  request, RestResponse<P> response) {
        Throwable cause = request.getCause().get();
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> trace(RestRequest<U, P>  request, RestResponse<P> response) {
        Throwable cause = request.getCause().get();
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> patch(RestRequest<U, P>  request, RestResponse<P> response) {
        Throwable cause = request.getCause().get();
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> head(RestRequest<U, P>  request, RestResponse<P> response) {
        Throwable cause = request.getCause().get();
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }
}

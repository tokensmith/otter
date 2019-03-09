package org.rootservices.otter.controller;

import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;

import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;

import java.util.Optional;

public abstract class RestErrorResource<U extends DefaultUser, P> {

    public abstract Optional<P> to(Throwable from);
    public abstract StatusCode statusCode();

    public RestResponse<P> get(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> post(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> put(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> delete(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> connect(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> options(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> trace(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> patch(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }

    public RestResponse<P> head(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(statusCode());
        response.setPayload(to(cause));
        return response;
    }
}

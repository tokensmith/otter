package org.rootservices.otter.controller;

import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;

import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;

public class RestErrorResource<U extends DefaultUser, P> {

    public RestResponse<P> get(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> post(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> put(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> delete(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> connect(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> options(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> trace(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> patch(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> head(RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }
}

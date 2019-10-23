package net.tokensmith.otter.controller;

import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;

public class RestResource<U extends DefaultUser, P> {
    public RestResponse<P> get(RestRequest<U, P> request, RestResponse<P> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> post(RestRequest<U, P> request, RestResponse<P> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> put(RestRequest<U, P> request, RestResponse<P> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> delete(RestRequest<U, P> request, RestResponse<P> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> connect(RestRequest<U, P> request, RestResponse<P> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> options(RestRequest<U, P> request, RestResponse<P> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> trace(RestRequest<U, P> request, RestResponse<P> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> patch(RestRequest<U, P> request, RestResponse<P> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public RestResponse<P> head(RestRequest<U, P> request, RestResponse<P> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }
}

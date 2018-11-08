package org.rootservices.otter.controller;

import org.rootservices.otter.controller.entity.*;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;


public class Resource<S extends DefaultSession, U extends DefaultUser> {

    public Response<S> get(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> post(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> put(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> delete(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> connect(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> options(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> trace(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> patch(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> head(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }
}

package org.rootservices.otter.controller;

import org.rootservices.otter.controller.entity.*;
import org.rootservices.otter.translatable.Translatable;


public class Resource<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {

    public Response<S> get(Request<S, U, P> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> post(Request<S, U, P> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> put(Request<S, U, P> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> delete(Request<S, U, P> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> connect(Request<S, U, P> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> options(Request<S, U, P> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> trace(Request<S, U, P> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> patch(Request<S, U, P> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<S> head(Request<S, U, P> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }
}

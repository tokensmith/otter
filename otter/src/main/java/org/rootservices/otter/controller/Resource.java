package org.rootservices.otter.controller;

import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.security.session.Session;


public class Resource<T extends Session> {

    public Response<T> get(Request<T> request, Response<T> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<T> post(Request<T> request, Response<T> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<T> put(Request<T> request, Response<T> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<T> delete(Request<T> request, Response<T> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<T> connect(Request<T> request, Response<T> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<T> options(Request<T> request, Response<T> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<T> trace(Request<T> request, Response<T> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<T> patch(Request<T> request, Response<T> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    public Response<T> head(Request<T> request, Response<T> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }
}

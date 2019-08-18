package org.rootservices.otter.dispatch.builder;

import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.dispatch.entity.either.ResponseError;


public class ResponseErrorBuilder<S, U> {

    private Request<S, U> request;
    private Response<S> response;
    private Throwable cause;
    private ResponseError.ErrorType errorType;


    public ResponseErrorBuilder<S, U> request(Request<S, U> request) {
        this.request = request;
        return this;
    }

    public ResponseErrorBuilder<S, U> response(Response<S> response) {
        this.response = response;
        return this;
    }

    public ResponseErrorBuilder<S, U> cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public ResponseErrorBuilder<S, U> errorType(ResponseError.ErrorType errorType) {
        this.errorType = errorType;
        return this;
    }

    public ResponseError<S, U> build() {
        return new ResponseError<>(request, response, cause, errorType);
    }

}

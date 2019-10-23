package net.tokensmith.otter.dispatch.entity.either;

import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;

public class ResponseError<S, U> {
    private Request<S, U> request;
    private Response<S> response;
    private Throwable cause;
    private ErrorType errorType;

    public ResponseError(Request<S, U> request, Response<S> response, Throwable cause, ErrorType errorType) {
        this.request = request;
        this.response = response;
        this.cause = cause;
        this.errorType = errorType;
    }

    public Request<S, U> getRequest() {
        return request;
    }

    public void setRequest(Request<S, U> request) {
        this.request = request;
    }

    public Response<S> getResponse() {
        return response;
    }

    public void setResponse(Response<S> response) {
        this.response = response;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public enum ErrorType {
        HALT, SERVER
    }
}

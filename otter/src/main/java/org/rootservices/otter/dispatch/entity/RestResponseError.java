package org.rootservices.otter.dispatch.entity;

import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;


public class RestResponseError<U, P> {
    private RestBtwnRequest<U> btwnRequest;
    private RestBtwnResponse btwnResponse;
    private RestRequest<U, P> requestForResource;
    private RestResponse<P> responseForResource;
    private RestResponse<P> resourceResponse;
    private RestResponse<P> response;
    private Throwable cause;
    private ErrorType errorType;


    public RestResponseError(RestBtwnRequest<U> btwnRequest, RestBtwnResponse btwnResponse, RestRequest<U, P> requestForResource, RestResponse<P> responseForResource, RestResponse<P> resourceResponse, RestResponse<P> response, Throwable cause, ErrorType errorType) {
        this.btwnRequest = btwnRequest;
        this.btwnResponse = btwnResponse;
        this.requestForResource = requestForResource;
        this.responseForResource = responseForResource;
        this.resourceResponse = resourceResponse;
        this.response = response;
        this.cause = cause;
        this.errorType = errorType;
    }

    public RestBtwnRequest<U> getBtwnRequest() {
        return btwnRequest;
    }

    public void setBtwnRequest(RestBtwnRequest<U> btwnRequest) {
        this.btwnRequest = btwnRequest;
    }

    public RestBtwnResponse getBtwnResponse() {
        return btwnResponse;
    }

    public void setBtwnResponse(RestBtwnResponse btwnResponse) {
        this.btwnResponse = btwnResponse;
    }

    public RestRequest<U, P> getRequestForResource() {
        return requestForResource;
    }

    public void setRequestForResource(RestRequest<U, P> requestForResource) {
        this.requestForResource = requestForResource;
    }

    public RestResponse<P> getResponseForResource() {
        return responseForResource;
    }

    public void setResponseForResource(RestResponse<P> responseForResource) {
        this.responseForResource = responseForResource;
    }

    public RestResponse<P> getResourceResponse() {
        return resourceResponse;
    }

    public void setResourceResponse(RestResponse<P> resourceResponse) {
        this.resourceResponse = resourceResponse;
    }

    public RestResponse<P> getResponse() {
        return response;
    }

    public void setResponse(RestResponse<P> response) {
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
        HALT, CLIENT, BAD_REQUEST, SERVER
    }
}

package org.rootservices.otter.dispatch;

import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestBtwnRequest;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.dispatch.entity.RestResponseError;

public class RestResponseErrorBuilder<U, P> {

    private RestBtwnRequest<U> btwnRequest;
    private RestBtwnResponse btwnResponse;
    private RestRequest<U, P> requestForResource;
    private RestResponse<P> responseForResource;
    private RestResponse<P> resourceResponse;
    private RestResponse<P> response;
    private Throwable cause;

    public RestResponseErrorBuilder<U, P> btwnRequest(RestBtwnRequest<U> btwnRequest) {
        this.btwnRequest = btwnRequest;
        return this;
    }

    public RestResponseErrorBuilder<U, P> btwnResponse(RestBtwnResponse btwnResponse) {
        this.btwnResponse = btwnResponse;
        return this;
    }

    public RestResponseErrorBuilder<U, P> requestForResource(RestRequest<U, P> requestForResource) {
        this.requestForResource = requestForResource;
        return this;
    }

    public RestResponseErrorBuilder<U, P> responseForResource(RestResponse<P> responseForResource) {
        this.responseForResource = responseForResource;
        return this;
    }

    public RestResponseErrorBuilder<U, P> resourceResponse(RestResponse<P> resourceResponse) {
        this.resourceResponse = resourceResponse;
        return this;
    }

    public RestResponseErrorBuilder<U, P> response(RestResponse<P> response) {
        this.response = response;
        return this;
    }

    public RestResponseErrorBuilder<U, P> cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public RestResponseError<U, P> build() {
        return new RestResponseError<U, P>(
            btwnRequest, btwnResponse, requestForResource, responseForResource, resourceResponse, response, cause
        );
    }
}

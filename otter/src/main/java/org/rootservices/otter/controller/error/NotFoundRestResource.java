package org.rootservices.otter.controller.error;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.builder.ClientErrorBuilder;
import org.rootservices.otter.controller.entity.ClientError;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;

import java.util.ArrayList;
import java.util.Optional;


public class NotFoundRestResource<U extends DefaultUser> extends RestResource<U, ClientError> {

    protected ClientError to(RestRequest<U, ClientError> from) {
        ClientError to = new ClientErrorBuilder()
                .source(ClientError.Source.URL)
                .actual(from.getPathWithParams())
                .build();
        return to;
    }
    
    protected StatusCode statusCode() {
        return StatusCode.NOT_FOUND;
    }

    @Override
    public RestResponse<ClientError> get(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(statusCode());
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> post(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(statusCode());
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> put(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(statusCode());
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> delete(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(statusCode());
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> connect(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(statusCode());
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> options(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(statusCode());
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> trace(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(statusCode());
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> patch(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(statusCode());
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> head(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(statusCode());
        response.setPayload(Optional.of(to(request)));
        return response;
    }

}

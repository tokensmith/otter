package net.tokensmith.otter.controller.error.rest;

import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.builder.ClientErrorBuilder;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.controller.header.Header;

import java.util.Optional;
import java.util.stream.Collectors;


public class MediaTypeRestResource<U extends DefaultUser> extends RestResource<U, ClientError> {

    protected StatusCode statusCode() {
        return StatusCode.UNSUPPORTED_MEDIA_TYPE;
    }
    
    protected ClientError to(RestRequest<U, ClientError> from) {
        String actual = null;
        if (from.getContentType() != null && from.getContentType().getType() != null) {
            actual = from.getContentType().toString();
        }
        ClientError to = new ClientErrorBuilder()
                .source(ClientError.Source.HEADER)
                .key(Header.CONTENT_TYPE.toString())
                .actual(actual)
                .expected(from.getPossibleContentTypes().stream()
                        .map( Object::toString )
                        .collect(Collectors.toList()))
                .build();
        return to;
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
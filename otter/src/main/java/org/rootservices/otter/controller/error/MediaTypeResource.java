package org.rootservices.otter.controller.error;

import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.ClientError;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.controller.header.Header;

import java.util.Optional;
import java.util.stream.Collectors;


public class MediaTypeResource<U extends DefaultUser> extends RestResource<U, ClientError> {

    protected ClientError to(RestRequest<U, ClientError> from) {
        ClientError to = new ClientError(
                ClientError.Source.HEADER,
                Header.CONTENT_TYPE.toString(),
                from.getContentType().toString(),
                from.getPossibleContentTypes().stream()
                    .map( Object::toString )
                    .collect(Collectors.toList())
        );
        return to;
    }

    @Override
    public RestResponse<ClientError> get(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> post(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> put(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> delete(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> connect(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> options(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> trace(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> patch(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ClientError> head(RestRequest<U, ClientError> request, RestResponse<ClientError> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setPayload(Optional.of(to(request)));
        return response;
    }
}
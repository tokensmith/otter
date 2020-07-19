package net.tokensmith.otter.controller.error.rest;

import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.ServerError;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class ServerErrorRestResource<U extends DefaultUser> extends RestResource<U, ServerError> {
    protected static Logger LOGGER = LoggerFactory.getLogger(ServerErrorRestResource.class);
    public static String RESPONSE_MESSAGE = "An unexpected error occurred.";
    public static String NO_CAUSE = "No cause found for the error.";

    protected ServerError to(RestRequest<U, ServerError> from) {
        ServerError to = new ServerError(RESPONSE_MESSAGE);
        return to;
    }

    protected void log(RestRequest<U, ServerError> request) {
        if (request.getCause().isPresent()) {
            LOGGER.error(request.getCause().get().getMessage(), request.getCause().get());
        } else {
            LOGGER.error(NO_CAUSE);
        }
    }
    @Override
    public RestResponse<ServerError> get(RestRequest<U, ServerError> request, RestResponse<ServerError> response) {
        log(request);
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ServerError> post(RestRequest<U, ServerError> request, RestResponse<ServerError> response) {
        log(request);
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ServerError> put(RestRequest<U, ServerError> request, RestResponse<ServerError> response) {
        log(request);
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ServerError> delete(RestRequest<U, ServerError> request, RestResponse<ServerError> response) {
        log(request);
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ServerError> connect(RestRequest<U, ServerError> request, RestResponse<ServerError> response) {
        log(request);
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ServerError> options(RestRequest<U, ServerError> request, RestResponse<ServerError> response) {
        log(request);
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ServerError> trace(RestRequest<U, ServerError> request, RestResponse<ServerError> response) {
        log(request);
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ServerError> patch(RestRequest<U, ServerError> request, RestResponse<ServerError> response) {
        log(request);
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setPayload(Optional.of(to(request)));
        return response;
    }

    @Override
    public RestResponse<ServerError> head(RestRequest<U, ServerError> request, RestResponse<ServerError> response) {
        log(request);
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setPayload(Optional.of(to(request)));
        return response;
    }
}

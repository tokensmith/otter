package org.rootservices.otter.controller;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.entity.*;
import org.rootservices.otter.controller.exception.DeserializationException;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;


import java.io.ByteArrayOutputStream;
import java.util.Optional;

public class RestResource<T, S extends DefaultSession, U extends DefaultUser> extends Resource<S, U> {
    protected static Logger logger = LogManager.getLogger(RestResource.class);

    protected JsonTranslator<T> translator;

    private static final String DUPLICATE_KEY_MSG = "Duplicate Key";
    private static final String INVALID_VALUE_MSG = "Invalid Value";
    private static final String UNKNOWN_KEY_MSG = "Unknown Key";
    private static final String INVALID_PAYLOAD_MSG = "Invalid Payload";

    private static final String DUPLICATE_KEY_DESC = "%s was repeated";
    private static final String INVALID_VALUE_DESC = "%s was invalid";
    private static final String UNKNOWN_KEY_DESC = "%s was not expected";


    public RestResource() {
    }

    public RestResource(JsonTranslator<T> translator) {
        this.translator = translator;
    }

    @Override
    public Response<S> get(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    @Override
    public Response<S> post(Request<S, U> request, Response<S> response) {
        T entity;

        try {
            entity = makeEntity(request.getBody().get());
        } catch (DeserializationException e) {
            logger.debug(e.getMessage(), e);
            Optional<ByteArrayOutputStream> payload = makeError(e);
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setPayload(payload);
            return response;
        }

        return post(request, response, entity);
    }

    @Override
    public Response<S> put(Request<S, U> request, Response<S> response) {
        T entity;

        try {
            entity = makeEntity(request.getBody().get());
        } catch (DeserializationException e) {
            logger.debug(e.getMessage(), e);
            Optional<ByteArrayOutputStream> payload = makeError(e);
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setPayload(payload);
            return response;
        }

        return put(request, response, entity);
    }

    @Override
    public Response<S> delete(Request<S, U> request, Response<S> response) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    @Override
    public Response<S> patch(Request<S, U> request, Response<S> response) {
        T entity;

        try {
            entity = makeEntity(request.getBody().get());
        } catch (DeserializationException e) {
            logger.debug(e.getMessage(), e);
            Optional<ByteArrayOutputStream> payload = makeError(e);
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setPayload(payload);
            return response;
        }

        return patch(request, response, entity);
    }

    protected Optional<ByteArrayOutputStream> makeError(DeserializationException e) {

        Optional<ByteArrayOutputStream> payload = Optional.empty();
        ErrorPayload errorPayload = new ErrorPayload(e.getMessage(), e.getDescription());
        try {
            ByteArrayOutputStream out = translator.to(errorPayload);
            payload = Optional.of(out);
        } catch (ToJsonException e1) {
            logger.error(e1.getMessage(), e1);
        }
        return payload;
    }

    protected T makeEntity(byte[] json) throws DeserializationException {
        T entity;

        try{
            entity = translator.from(json);
        } catch (DuplicateKeyException e) {
            String desc = String.format(DUPLICATE_KEY_DESC, e.getKey());
            throw new DeserializationException(DUPLICATE_KEY_MSG, e, desc);
        } catch (InvalidValueException e) {
            String desc = String.format(INVALID_VALUE_DESC, e.getKey());
            throw new DeserializationException(INVALID_VALUE_MSG, e, desc);
        } catch (UnknownKeyException e) {
            String desc = String.format(UNKNOWN_KEY_DESC, e.getKey());
            throw new DeserializationException(UNKNOWN_KEY_MSG, e, desc);
        } catch (InvalidPayloadException e) {
            throw new DeserializationException(INVALID_PAYLOAD_MSG, e, null);
        }
        return entity;

    }

    protected Response<S> post(Request<S, U> request, Response<S> response, T entity) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    protected Response<S> put(Request<S, U> request, Response<S> response, T entity) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }

    protected Response<S> patch(Request<S, U> request, Response<S> response, T entity) {
        response.setStatusCode(StatusCode.NOT_IMPLEMENTED);
        return response;
    }
}

package org.rootservices.otter.controller;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.entity.*;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;


import java.io.ByteArrayOutputStream;
import java.util.Optional;

public class LegacyRestResource<T, S extends DefaultSession, U extends DefaultUser> extends Resource<S, U> {
    protected static Logger logger = LogManager.getLogger(LegacyRestResource.class);

    protected JsonTranslator<T> translator;

    private static final String DUPLICATE_KEY_MSG = "Duplicate Key";
    private static final String INVALID_VALUE_MSG = "Invalid Value";
    private static final String UNKNOWN_KEY_MSG = "Unknown Key";
    private static final String INVALID_PAYLOAD_MSG = "Invalid Payload";

    private static final String DUPLICATE_KEY_DESC = "%s was repeated";
    private static final String INVALID_VALUE_DESC = "%s was invalid";
    private static final String UNKNOWN_KEY_DESC = "%s was not expected";


    public LegacyRestResource() {
    }

    public LegacyRestResource(JsonTranslator<T> translator) {
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
            Optional<byte[]> payload = makeError(e);
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
            Optional<byte[]> payload = makeError(e);
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
            Optional<byte[]> payload = makeError(e);
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setPayload(payload);
            return response;
        }

        return patch(request, response, entity);
    }

    protected Optional<byte[]> makeError(DeserializationException e) {

        Optional<byte[]> payload = Optional.empty();

        String description = "Unknown error occurred";
        if (Reason.DUPLICATE_KEY.equals(e.getReason())) {
            description = String.format(DUPLICATE_KEY_DESC, e.getKey().get());
        } else if (Reason.INVALID_VALUE.equals(e.getReason())) {
            description = String.format(INVALID_VALUE_DESC, e.getKey().get());
        } else if (Reason.UNKNOWN_KEY.equals(e.getReason())) {
            description = String.format(UNKNOWN_KEY_DESC, e.getKey().get());
        } else if (Reason.INVALID_PAYLOAD.equals(e.getReason())) {
            description = "Payload invalid";
        }

        ErrorPayload errorPayload = new ErrorPayload(e.getMessage(), description);
        try {
            byte[] out = translator.to(errorPayload);
            payload = Optional.of(out);
        } catch (ToJsonException e1) {
            logger.error(e1.getMessage(), e1);
        }
        return payload;
    }

    protected T makeEntity(byte[] json) throws DeserializationException {
        T entity;

        try{
            entity = translator.fromWithSpecificCause(json);
        } catch (DuplicateKeyException e) {
            throw new DeserializationException(DUPLICATE_KEY_MSG, e.getKey(), Reason.DUPLICATE_KEY, e);
        } catch (InvalidValueException e) {
            throw new DeserializationException(INVALID_VALUE_MSG, e.getKey(), Reason.INVALID_VALUE, e);
        } catch (UnknownKeyException e) {
            throw new DeserializationException(UNKNOWN_KEY_MSG, e.getKey(), Reason.UNKNOWN_KEY, e);
        } catch (InvalidPayloadException e) {
            throw new DeserializationException(INVALID_PAYLOAD_MSG, Reason.INVALID_PAYLOAD, e);
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

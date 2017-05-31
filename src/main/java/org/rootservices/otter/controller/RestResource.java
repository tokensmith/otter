package org.rootservices.otter.controller;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Error;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.exception.DeserializationException;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;

import java.io.BufferedReader;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

public class RestResource<T> extends Resource {
    protected static Logger logger = LogManager.getLogger(RestResource.class);

    protected JsonTranslator<T> translator;
    protected Class<T> type;

    private static final String DUPLICATE_KEY_MSG = "Duplicate Key";
    private static final String INVALID_VALUE_MSG = "Invalid Value";
    private static final String UNKNOWN_KEY_MSG = "Unknown Key";
    private static final String INVALID_PAYLOAD_MSG = "Invalid Payload";

    private static final String DUPLICATE_KEY_DESC = "%s was repeated";
    private static final String INVALID_VALUE_DESC = "%s was invalid";
    private static final String UNKNOWN_KEY_DESC = "%s was not expected";

    public RestResource() {
        if(this.type == null) {
            this.type = (Class<T>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
        }
    }

    public void setTranslator(JsonTranslator<T> translator) {
        this.translator = translator;
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public Response get(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    @Override
    public Response post(Request request) {
        T entity;

        try {
            entity = makeEntity(request.getBody());
        } catch (DeserializationException e) {
            logger.debug(e.getMessage(), e);
            Optional<String> body = makeError(e);
            ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
            return responseBuilder.setBody(body).badRequest().build();
        }

        return post(request, entity);
    }

    @Override
    public Response put(Request request) {
        T entity;

        try {
            entity = makeEntity(request.getBody());
        } catch (DeserializationException e) {
            logger.debug(e.getMessage(), e);
            Optional<String> body = makeError(e);
            ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
            return responseBuilder.setBody(body).badRequest().build();
        }

        return put(request, entity);
    }

    @Override
    public Response delete(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    @Override
    public Response patch(Request request) {
        T entity;

        try {
            entity = makeEntity(request.getBody());
        } catch (DeserializationException e) {
            logger.debug(e.getMessage(), e);
            Optional<String> body = makeError(e);
            ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
            return responseBuilder.setBody(body).badRequest().build();
        }

        return patch(request, entity);
    }

    protected Optional<String> makeError(DeserializationException e) {

        Optional<String> body = Optional.empty();
        Error error = new Error(e.getMessage(), e.getDescription());
        try {
            String response = translator.to(error);
            body = Optional.of(response);
        } catch (ToJsonException e1) {
            logger.error(e1.getMessage(), e1);
        }
        return body;
    }

    protected T makeEntity(BufferedReader json) throws DeserializationException {
        T entity;
        try{
            entity = translator.from(json, type);
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

    protected Response post(Request request, T entity) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    protected Response put(Request request, T entity) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    protected Response patch(Request request, T entity) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }
}
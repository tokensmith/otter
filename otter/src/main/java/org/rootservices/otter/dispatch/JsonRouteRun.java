package org.rootservices.otter.dispatch;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.ErrorPayload;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestBtwnRequest;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.dispatch.entity.RestErrorResponse;
import org.rootservices.otter.dispatch.exception.ClientException;
import org.rootservices.otter.dispatch.exception.ServerException;
import org.rootservices.otter.dispatch.exception.WrongTypeException;
import org.rootservices.otter.dispatch.translator.RestErrorHandler;
import org.rootservices.otter.dispatch.translator.rest.*;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JsonRouteRun<U extends DefaultUser, P> implements RouteRunner  {
    protected static Logger logger = LogManager.getLogger(JsonRouteRun.class);
    private RestRoute<U, P> restRoute;
    private RestResponseTranslator<P> restResponseTranslator;
    private RestRequestTranslator<U, P> restRequestTranslator;
    private RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator;
    private RestBtwnResponseTranslator<P> restBtwnResponseTranslator;
    private JsonTranslator<P> jsonTranslator;

    // error handling dependencies
    private Map<StatusCode, RestErrorHandler<U>> errorHandlers;
    private RestErrorRequestTranslator<U> errorRequestTranslator;
    private RestErrorResponseTranslator errorResponseTranslator;

    // default error messaging.
    private static final String DUPLICATE_KEY_DESC = "%s was repeated";
    private static final String INVALID_VALUE_DESC = "%s was invalid";
    private static final String UNKNOWN_KEY_DESC = "%s was not expected";

    public JsonRouteRun(RestRoute<U, P> restRoute, RestResponseTranslator<P> restResponseTranslator, RestRequestTranslator<U, P> restRequestTranslator, RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator, RestBtwnResponseTranslator<P> restBtwnResponseTranslator, JsonTranslator<P> jsonTranslator, Map<StatusCode, RestErrorHandler<U>> errorHandlers, RestErrorRequestTranslator<U> errorRequestTranslator, RestErrorResponseTranslator errorResponseTranslator) {
        this.restRoute = restRoute;
        this.restResponseTranslator = restResponseTranslator;
        this.restRequestTranslator = restRequestTranslator;
        this.restBtwnRequestTranslator = restBtwnRequestTranslator;
        this.restBtwnResponseTranslator = restBtwnResponseTranslator;
        this.jsonTranslator = jsonTranslator;

        // error handling dependencies
        this.errorHandlers = errorHandlers;
        this.errorRequestTranslator = errorRequestTranslator;
        this.errorResponseTranslator = errorResponseTranslator;
    }

    @Override
    public Answer run(Ask ask, Answer answer) throws HaltException {

        try {
            answer = process(ask, answer);
        } catch (ClientException e) {
            Optional<Answer> answerFromHandler = handle(StatusCode.BAD_REQUEST, e, ask, answer);
            if (!answerFromHandler.isPresent()) {
                // default 400 handling.
                Optional<byte[]> errorPayload = toDefaultBadRequest(e);
                answer.setStatusCode(StatusCode.BAD_REQUEST);
                answer.setPayload(errorPayload);
                throw new HaltException(e.getMessage(), e);
            }
            answer = answerFromHandler.get();
        } catch (ServerException e) {
            answer = handleServerError(e, ask, answer);
        } catch (Throwable e) {
            answer = handleServerError(e, ask, answer);
        }
        return answer;
    }

    protected Answer process(Ask ask, Answer answer) throws ClientException, ServerException, HaltException {

        Optional<P> entity;
        try {
            entity = makeEntity(ask.getBody());
        } catch (DeserializationException e) {
            throw new ClientException("Could not serialize request body", e);
        }

        RestBtwnRequest<U> btwnRequest = restBtwnRequestTranslator.to(ask);
        RestBtwnResponse btwnResponse = restBtwnResponseTranslator.to(answer);

        // send it off to betweens and rest resource
        RestResponse<P> runResponse;
        try {
            runResponse = executeResourceMethod(restRoute, btwnRequest, btwnResponse, entity);
        } catch (WrongTypeException e) {
            throw new ServerException("", e);
        } catch (HaltException e) {
            // btwnResponse may have been updated in a between. need to merge it with answer.
            // TODO: throw client or server exception for handling, or have it indicate which handler
            answer = restBtwnResponseTranslator.from(answer, btwnResponse);
            throw e;
        }

        // response entity marshalling
        answer = restResponseTranslator.from(answer, runResponse);
        Optional<byte[]> out = payloadToBytes(runResponse.getPayload());
        answer.setPayload(out);

        return answer;
    }

    protected Optional<P> makeEntity(Optional<byte[]> body) throws DeserializationException {
        Optional<P> entity = Optional.empty();

        if (body.isPresent()) {
            try {
                entity = Optional.of(jsonTranslator.from(body.get()));
            } catch (DeserializationException e) {
                throw e;
            }
        }
        return entity;
    }

    protected Optional<Answer> handle(StatusCode statusCode, Throwable cause, Ask ask, Answer answer) {
        Optional<Answer> answerFromHandler = Optional.empty();
        RestErrorHandler<U> errorHandler = errorHandlers.get(statusCode);
        if (errorHandler != null) {
            RestErrorRequest<U> errorReq = errorRequestTranslator.to(ask);
            RestErrorResponse errorResp = errorResponseTranslator.to(answer);
            answerFromHandler = Optional.of(errorHandler.run(errorReq, errorResp, cause));
        }

        return answerFromHandler;
    }

    protected Answer handleServerError(Throwable cause, Ask ask, Answer answer) throws HaltException {
        Optional<Answer> answerFromHandler = handle(StatusCode.SERVER_ERROR, cause, ask, answer);
        if ( !answerFromHandler.isPresent() ) {
            throw new HaltException(cause.getMessage(), cause);
        }
        return answerFromHandler.get();
    }

    protected Optional<byte[]> toDefaultBadRequest(ClientException from) {
        DeserializationException cause = (DeserializationException) from.getCause();

        Optional<byte[]> to = Optional.empty();
        String description = "Unknown error occurred";
        if (Reason.DUPLICATE_KEY.equals(cause.getReason())) {
            description = String.format(DUPLICATE_KEY_DESC, cause.getKey().get());
        } else if (Reason.INVALID_VALUE.equals(cause.getReason())) {
            description = String.format(INVALID_VALUE_DESC, cause.getKey().get());
        } else if (Reason.UNKNOWN_KEY.equals(cause.getReason())) {
            description = String.format(UNKNOWN_KEY_DESC, cause.getKey().get());
        } else if (Reason.INVALID_PAYLOAD.equals(cause.getReason())) {
            description = "Payload invalid";
        }

        ErrorPayload errorPayload = new ErrorPayload(cause.getMessage(), description);
        try {
            byte[] out = jsonTranslator.to(errorPayload);
            to = Optional.of(out);
        } catch (ToJsonException e1) {
            logger.error(e1.getMessage(), e1);
        }
        return to;
    }

    protected RestResponse<P> executeResourceMethod(RestRoute<U, P> route, RestBtwnRequest<U> btwnRequest, RestBtwnResponse btwnResponse, Optional<P> entity) throws HaltException, WrongTypeException {

        RestResource<U, P> resource = route.getRestResource();
        RestRequest<U, P> requestForResource;
        RestResponse<P> responseForResource;
        Method method = btwnRequest.getMethod();
        RestResponse<P> resourceResponse = null;
        RestResponse<P> response;

        try {
            executeBetween(route.getBefore(), method, btwnRequest, btwnResponse);
        } catch (HaltException e) {
            throw e;
        }
        requestForResource = restRequestTranslator.to(btwnRequest, entity);
        responseForResource = restResponseTranslator.to(btwnResponse);

        if (method == Method.GET) {
            resourceResponse = resource.get(requestForResource, responseForResource);
        } else if (method == Method.POST) {
            resourceResponse = resource.post(requestForResource, responseForResource);
        } else if (method == Method.PUT) {
            resourceResponse = resource.put(requestForResource, responseForResource);
        } else if (method == Method.PATCH) {
            resourceResponse = resource.patch(requestForResource, responseForResource);
        } else if (method == Method.DELETE) {
            resourceResponse = resource.delete(requestForResource, responseForResource);
        } else if (method == Method.CONNECT) {
            resourceResponse = resource.connect(requestForResource, responseForResource);
        } else if (method == Method.OPTIONS) {
            resourceResponse = resource.options(requestForResource, responseForResource);
        } else if (method == Method.TRACE) {
            resourceResponse = resource.trace(requestForResource, responseForResource);
        } else if (method == Method.HEAD) {
            resourceResponse = resource.head(requestForResource, responseForResource);
        }

        RestBtwnRequest<U> btwnRequestForAfter = restBtwnRequestTranslator.to(requestForResource);

        Optional<byte[]> resourceResponsePayload = payloadToBytes(resourceResponse.getPayload());
        RestBtwnResponse btwnResponseForAfter = restBtwnResponseTranslator.to(resourceResponse, resourceResponsePayload);

        try {
            executeBetween(route.getAfter(), method, btwnRequestForAfter, btwnResponseForAfter);
        } catch (HaltException e) {
            // TODO: translate to client or server exception for handling.
            throw e;
        }

        response = restResponseTranslator.to(btwnResponseForAfter);

        if (isPayloadDirty(resourceResponsePayload, btwnResponseForAfter.getPayload())) {
            Optional<P> responseEntity;

            try {
                responseEntity = makeEntity(btwnResponseForAfter.getPayload());
            } catch (DeserializationException e) {
                throw new WrongTypeException(
                    "Could not marshal payload assigned in after between to the desired type",
                    e
                );
            }

            response.setPayload(responseEntity);
        } else {
            response.setPayload(resourceResponse.getPayload());
        }

        return response;
    }

    protected boolean isPayloadDirty(Optional<byte[]> resourcePayload, Optional<byte[]> btwnPayload)  {
        boolean isDirty = false;

        if (!resourcePayload.isPresent() && btwnPayload.isPresent()) {
            // resource has no payload but btwn has one.
            isDirty = true;
        } else if (resourcePayload.isPresent() &&
                btwnPayload.isPresent() &&
                ! Arrays.equals(resourcePayload.get(), btwnPayload.get())) {
            // both have payloads but they are not equal.
            isDirty = true;
        }
        return isDirty;
    }

    protected void executeBetween(List<RestBetween<U>> betweens, Method method, RestBtwnRequest<U> btwnRequest, RestBtwnResponse btwnResponse) throws HaltException {

        for(RestBetween<U> between: betweens) {
            try {
                between.process(method, btwnRequest, btwnResponse);
            } catch(HaltException e) {
                throw e;
            }
        }
    }

    protected Optional<byte[]> payloadToBytes(Optional<P> payload) {
        Optional<byte[]> out = Optional.empty();
        try {
            out = Optional.of(jsonTranslator.to(payload));
        } catch (ToJsonException e) {
            logger.error(e.getMessage(), e);
        }

        return out;
    }

    public RestRoute<U, P> getRestRoute() {
        return restRoute;
    }
}

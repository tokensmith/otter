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
import org.rootservices.otter.dispatch.translator.rest.RestBtwnRequestTranslator;
import org.rootservices.otter.dispatch.translator.rest.RestBtwnResponseTranslator;
import org.rootservices.otter.dispatch.translator.rest.RestRequestTranslator;
import org.rootservices.otter.dispatch.translator.rest.RestResponseTranslator;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JsonRouteRun<U extends DefaultUser, P> implements RouteRunner  {
    protected static Logger logger = LogManager.getLogger(JsonRouteRun.class);
    private RestRoute<U, P> restRoute;
    private RestResponseTranslator<P> restResponseTranslator;
    private RestRequestTranslator<U, P> restRequestTranslator;
    private RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator;
    private RestBtwnResponseTranslator<P> restBtwnResponseTranslator;
    protected JsonTranslator<P> jsonTranslator;

    public JsonRouteRun(RestRoute<U, P> restRoute, RestResponseTranslator<P> restResponseTranslator, RestRequestTranslator<U, P> restRequestTranslator, RestBtwnRequestTranslator<U, P> restBtwnRequestTranslator, RestBtwnResponseTranslator<P> restBtwnResponseTranslator, JsonTranslator<P> jsonTranslator) {
        this.restRoute = restRoute;
        this.restResponseTranslator = restResponseTranslator;
        this.restRequestTranslator = restRequestTranslator;
        this.restBtwnRequestTranslator = restBtwnRequestTranslator;
        this.restBtwnResponseTranslator = restBtwnResponseTranslator;
        this.jsonTranslator = jsonTranslator;
    }

    @Override
    public Answer run(Ask ask, Answer answer) throws HaltException {

        RestBtwnRequest<U> btwnRequest = restBtwnRequestTranslator.to(ask);
        RestBtwnResponse btwnResponse = restBtwnResponseTranslator.to(answer);

        // request entity marshalling.
        Optional<P> entity;
        try {
            entity = makeEntity(ask.getBody());
        } catch (HaltException e) {
            // Error Handling: 400, maybe push this to engine by throwing an exception?
            Optional<byte[]> errorPayload = makeError((DeserializationException) e.getCause());
            btwnResponse.setStatusCode(StatusCode.BAD_REQUEST);

            answer = restBtwnResponseTranslator.from(answer, btwnResponse);
            answer.setPayload(errorPayload);
            throw e;
        }

        // send it off to betweens and rest resource
        RestResponse<P> runResponse;
        try {
            runResponse = executeResourceMethod(restRoute, btwnRequest, btwnResponse, entity);
        } catch (HaltException e) {
            // btwnResponse may have been updated in a between. need to merge it with answer.
            answer = restBtwnResponseTranslator.from(answer, btwnResponse);
            throw e;
        }

        // response entity marshalling
        answer = restResponseTranslator.from(answer, runResponse);
        Optional<byte[]> out = payloadToBytes(runResponse.getPayload());
        answer.setPayload(out);

        return answer;
    }

    protected Optional<P> makeEntity(Optional<byte[]> body) throws HaltException {
        Optional<P> entity = Optional.empty();

        if (body.isPresent()) {
            try {
                entity = Optional.of(jsonTranslator.from(body.get()));
            } catch (DeserializationException e) {
                logger.debug(e.getMessage(), e);
                throw new HaltException(e.getMessage(), e);
            }
        }
        return entity;
    }

    protected Optional<byte[]> makeError(DeserializationException e) {

        Optional<byte[]> payload = Optional.empty();
        ErrorPayload errorPayload = new ErrorPayload(e.getMessage(), e.getDescription());
        try {
            byte[] out = jsonTranslator.to(errorPayload);
            payload = Optional.of(out);
        } catch (ToJsonException e1) {
            logger.error(e1.getMessage(), e1);
        }
        return payload;
    }

    protected RestResponse<P> executeResourceMethod(RestRoute<U, P> route, RestBtwnRequest<U> btwnRequest, RestBtwnResponse btwnResponse, Optional<P> entity) throws HaltException {

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
            throw e;
        }

        response = restResponseTranslator.to(btwnResponseForAfter);

        if (isPayloadDirty(resourceResponsePayload, btwnResponseForAfter.getPayload())) {
            Optional<P> responseEntity = makeEntity(btwnResponseForAfter.getPayload());
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

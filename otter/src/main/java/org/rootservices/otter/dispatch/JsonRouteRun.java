package org.rootservices.otter.dispatch;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.ErrorPayload;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.translator.rest.RestRequestTranslator;
import org.rootservices.otter.dispatch.translator.rest.RestResponseTranslator;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.translatable.Translatable;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

public class JsonRouteRun<U extends DefaultUser, P extends Translatable> implements RouteRunner  {
    protected static Logger logger = LogManager.getLogger(JsonRouteRun.class);
    private RestRoute<U, P> restRoute;
    private RestResponseTranslator<P> restResponseTranslator;
    private RestRequestTranslator<U, P> restRequestTranslator;
    protected JsonTranslator<P> jsonTranslator;

    public JsonRouteRun(RestRoute<U, P> restRoute, RestResponseTranslator<P> restResponseTranslator, RestRequestTranslator<U, P> restRequestTranslator, JsonTranslator<P> jsonTranslator) {
        this.restRoute = restRoute;
        this.restResponseTranslator = restResponseTranslator;
        this.restRequestTranslator = restRequestTranslator;
        this.jsonTranslator = jsonTranslator;
    }

    @Override
    public Answer run(Ask ask, Answer answer) throws HaltException {
        RestRequest<U, P> request = restRequestTranslator.to(ask);
        RestResponse<P> response = restResponseTranslator.to(answer);


        // request entity marshalling.
        Optional<P> entity;
        try {
            entity = makeEntity(ask);
        } catch (HaltException e) {
            Optional<ByteArrayOutputStream> errorPayload = makeError((DeserializationException) e.getCause());
            response.setStatusCode(StatusCode.BAD_REQUEST);

            answer = restResponseTranslator.from(answer, response);
            answer.setPayload(errorPayload);
            throw e;
        }
        request.setPayload(entity);


        // send it off to betweens and rest resource
        RestResponse<P> runResponse;
        try {
            runResponse = executeResourceMethod(restRoute, request, response);
        } catch (HaltException e) {
            // response may have been updated in a between. need to merge it with answer.
            answer = restResponseTranslator.from(answer, response);
            throw e;
        }


        // response entity marshalling
        answer = restResponseTranslator.from(answer, runResponse);
        Optional<ByteArrayOutputStream> payload = Optional.empty();
        try {
            payload = Optional.of(jsonTranslator.to(response.getPayload()));
        } catch (ToJsonException e) {
            logger.error(e.getMessage(), e);
        }
        answer.setPayload(payload);
        return answer;
    }

    protected Optional<P> makeEntity(Ask ask) throws HaltException {
        Optional<P> entity = Optional.empty();

        if (ask.getBody().isPresent()) {
            try {
                entity = Optional.of(jsonTranslator.from(ask.getBody().get()));
            } catch (DeserializationException e) {
                logger.debug(e.getMessage(), e);
                throw new HaltException(e.getMessage(), e);
            }
        }
        return entity;
    }

    protected Optional<ByteArrayOutputStream> makeError(DeserializationException e) {

        Optional<ByteArrayOutputStream> payload = Optional.empty();
        ErrorPayload errorPayload = new ErrorPayload(e.getMessage(), e.getDescription());
        try {
            ByteArrayOutputStream out = jsonTranslator.to(errorPayload);
            payload = Optional.of(out);
        } catch (ToJsonException e1) {
            logger.error(e1.getMessage(), e1);
        }
        return payload;
    }

    protected RestResponse<P> executeResourceMethod(RestRoute<U, P> route, RestRequest<U, P> request, RestResponse<P> response) throws HaltException {
        RestResource<U, P> resource = route.getRestResource();
        RestResponse<P> resourceResponse = null;
        Method method = request.getMethod();

        try {
            executeBetween(route.getBefore(), method, request, response);
        } catch (HaltException e) {
            throw e;
        }

        if (method == Method.GET) {
            resourceResponse = resource.get(request, response);
        } else if (method == Method.POST) {
            resourceResponse = resource.post(request, response);
        } else if (method == Method.PUT) {
            resourceResponse = resource.put(request, response);
        } else if (method == Method.PATCH) {
            resourceResponse = resource.patch(request, response);
        } else if (method == Method.DELETE) {
            resourceResponse = resource.delete(request, response);
        } else if (method == Method.CONNECT) {
            resourceResponse = resource.connect(request, response);
        } else if (method == Method.OPTIONS) {
            resourceResponse = resource.options(request, response);
        } else if (method == Method.TRACE) {
            resourceResponse = resource.trace(request, response);
        } else if (method == Method.HEAD) {
            resourceResponse = resource.head(request, response);
        }

        try {
            executeBetween(route.getAfter(), method, request, resourceResponse);
        } catch (HaltException e) {
            throw e;
        }

        return resourceResponse;
    }

    protected void executeBetween(List<RestBetween<U, P>> betweens, Method method, RestRequest<U, P> request, RestResponse<P> response) throws HaltException {
        for(RestBetween<U, P> between: betweens) {
            try {
                between.process(method, request, response);
            } catch(HaltException e) {
                throw e;
            }
        }
    }

    public RestRoute<U, P> getRestRoute() {
        return restRoute;
    }
}

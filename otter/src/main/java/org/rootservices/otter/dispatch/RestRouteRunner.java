package org.rootservices.otter.dispatch;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.*;
import org.rootservices.otter.controller.exception.DeserializationException;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.translatable.Translatable;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;



public class RestRouteRunner<S extends DefaultSession, U extends DefaultUser, P extends Translatable> implements RouteRunner {
    protected static Logger logger = LogManager.getLogger(RestRouteRunner.class);
    public static final String DUPLICATE_KEY_MSG = "Duplicate Key";
    public static final String INVALID_VALUE_MSG = "Invalid Value";
    public static final String UNKNOWN_KEY_MSG = "Unknown Key";
    public static final String INVALID_PAYLOAD_MSG = "Invalid Payload";
    public static final String DUPLICATE_KEY_DESC = "%s was repeated";
    public static final String INVALID_VALUE_DESC = "%s was invalid";
    public static final String UNKNOWN_KEY_DESC = "%s was not expected";
    private JsonTranslator<P> jsonTranslator;

    private Route<S, U, P> route;
    private RequestTranslator<S, U, P> requestTranslator;
    private AnswerTranslator<S> answerTranslator;

    public RestRouteRunner(JsonTranslator<P> jsonTranslator, Route<S, U, P> route, RequestTranslator<S, U, P> requestTranslator, AnswerTranslator<S> answerTranslator) {
        this.jsonTranslator = jsonTranslator;
        this.route = route;
        this.requestTranslator = requestTranslator;
        this.answerTranslator = answerTranslator;
    }

    @Override
    public Answer run(Ask ask, Answer answer) throws HaltException {
        Request<S, U, P> request = requestTranslator.to(ask);
        Response<S> response = answerTranslator.from(answer);

        Response<S> runResponse;
        try {
            runResponse = executeResourceMethod(route, request, response);
        } catch (HaltException e) {
            // response may have been updated in a between.
            answer = answerTranslator.to(answer, response);
            throw e;
        }

        return answerTranslator.to(runResponse);
    }

    protected Response<S> executeResourceMethod(Route<S, U, P> route, Request<S, U, P> request, Response<S> response) throws HaltException {
        Resource<S, U, P> resource = route.getResource();
        Response<S> resourceResponse = null;
        Method method = request.getMethod();

        try {
            executeBetween(route.getBefore(), method, request, response);
        } catch (HaltException e) {
            throw e;
        }

        if (method == Method.GET) {
            resourceResponse = resource.get(request, response);
        } else if (method == Method.POST) {
            P payload = parsePayload(request, response);
            resourceResponse = resource.post(request, response);
        } else if (method == Method.PUT) {
            P payload = parsePayload(request, response);
            resourceResponse = resource.put(request, response);
        } else if (method == Method.PATCH) {
            P payload = parsePayload(request, response);
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

    protected void executeBetween(List<Between<S, U, P>> betweens, Method method, Request<S, U, P> request, Response<S> response) throws HaltException {
        for(Between<S, U, P> between: betweens) {
            try {
                between.process(method, request, response);
            } catch(HaltException e) {
                throw e;
            }
        }
    }

    protected P parsePayload(Request<S, U, P> request, Response<S> response) throws HaltException {
        P entity;

        try {
            entity = marshallBody(request.getBody().get());
        } catch (DeserializationException e) {
            logger.debug(e.getMessage(), e);
            Optional<ByteArrayOutputStream> payload = makeError(e);
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setPayload(payload);
            throw new HaltException(e.getMessage(), e);
        }
        return entity;
    }

    protected P marshallBody(byte[] body) throws DeserializationException {
        P entity;

        try{
            entity = jsonTranslator.from(body);
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

    // TODO: maybe pass in a method to targets to handle errors.
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

    public Route<S, U, P> getRoute() {
        return route;
    }

    public RequestTranslator<S, U, P> getRequestTranslator() {
        return requestTranslator;
    }

    public AnswerTranslator<S> getAnswerTranslator() {
        return answerTranslator;
    }
}

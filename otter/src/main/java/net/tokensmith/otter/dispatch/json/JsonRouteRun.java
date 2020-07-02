package net.tokensmith.otter.dispatch.json;


import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.dispatch.json.validator.ValidateError;
import net.tokensmith.otter.dispatch.json.validator.exception.ValidateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.dispatch.builder.RestResponseErrorBuilder;
import net.tokensmith.otter.dispatch.entity.*;
import net.tokensmith.otter.dispatch.entity.either.RestReponseEither;
import net.tokensmith.otter.dispatch.entity.either.RestResponseError;
import net.tokensmith.otter.dispatch.exception.ClientException;
import net.tokensmith.otter.dispatch.exception.ServerException;
import net.tokensmith.otter.dispatch.translator.RestErrorHandler;
import net.tokensmith.otter.dispatch.translator.rest.*;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.RestRoute;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.router.entity.io.Ask;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.exception.*;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class JsonRouteRun<S extends DefaultSession, U extends DefaultUser, P> implements RouteRunner {
    protected static Logger LOGGER = LoggerFactory.getLogger(JsonRouteRun.class);
    private RestRoute<S, U, P> restRoute;
    private RestResponseTranslator<P> restResponseTranslator;
    private RestRequestTranslator<S, U, P> restRequestTranslator;
    private RestBtwnRequestTranslator<S, U, P> restBtwnRequestTranslator;
    private RestBtwnResponseTranslator<P> restBtwnResponseTranslator;
    private JsonTranslator<P> jsonTranslator;
    private Validate validate;

    // error handling dependencies
    private Map<StatusCode, RestErrorHandler<U>> errorHandlers;
    private RestErrorRequestTranslator<S, U> errorRequestTranslator;
    private RestErrorResponseTranslator errorResponseTranslator;

    public JsonRouteRun() {
    }

    public JsonRouteRun(RestRoute<S, U, P> restRoute, RestResponseTranslator<P> restResponseTranslator, RestRequestTranslator<S, U, P> restRequestTranslator, RestBtwnRequestTranslator<S, U, P> restBtwnRequestTranslator, RestBtwnResponseTranslator<P> restBtwnResponseTranslator, JsonTranslator<P> jsonTranslator, Validate validate, Map<StatusCode, RestErrorHandler<U>> errorHandlers, RestErrorRequestTranslator<S, U> errorRequestTranslator, RestErrorResponseTranslator errorResponseTranslator) {
        this.restRoute = restRoute;
        this.restResponseTranslator = restResponseTranslator;
        this.restRequestTranslator = restRequestTranslator;
        this.restBtwnRequestTranslator = restBtwnRequestTranslator;
        this.restBtwnResponseTranslator = restBtwnResponseTranslator;
        this.jsonTranslator = jsonTranslator;
        this.validate = validate;

        // error handling dependencies
        this.errorHandlers = errorHandlers;
        this.errorRequestTranslator = errorRequestTranslator;
        this.errorResponseTranslator = errorResponseTranslator;
    }

    @Override
    public Answer run(Ask ask, Answer answer) throws HaltException {

        try {
            answer = process(ask, answer);
        } catch(HaltException e) {
            throw e;
        }
        return answer;
    }

    protected Answer process(Ask ask, Answer answer) throws HaltException {

        Optional<P> entity;
        try {
            entity = to(ask.getBody());
        } catch (DeserializationException e) {
            // May want to consider an alternative to prevent duplicate returns in this method.
            ClientException clientException = new ClientException("Could not serialize request body", e);
            RestResponseError<S, U, P> error = new RestResponseErrorBuilder<S, U, P>()
                    .cause(clientException)
                    .errorType(RestResponseError.ErrorType.BAD_REQUEST)
                    .build();
            return handleErrors(error, ask, answer);
        }

        try {
            validate(entity);
        } catch (ValidateException e) {
            // May want to consider an alternative to prevent duplicate returns in this method.
            ClientException clientException = new ClientException("Payload violated constraints", e);
            RestResponseError<S, U, P> error = new RestResponseErrorBuilder<S, U, P>()
                    .cause(clientException)
                    .errorType(RestResponseError.ErrorType.BAD_REQUEST)
                    .build();
            return handleErrors(error, ask, answer);
        }

        RestBtwnRequest<S, U> btwnRequest = restBtwnRequestTranslator.to(ask);
        RestBtwnResponse btwnResponse = restBtwnResponseTranslator.to(answer);

        RestReponseEither<S, U, P> runResponse = executeResourceMethod(restRoute, btwnRequest, btwnResponse, entity);

        if (runResponse.getLeft().isPresent()) {
            answer = handleErrors(runResponse.getLeft().get(), ask, answer);
        } else if (runResponse.getRight().isPresent() && runResponse.getRight().get().getRawPayload().isPresent()) {
            LOGGER.debug("using raw payload");
            answer = restResponseTranslator.from(answer, runResponse.getRight().get());
            answer.setPayload(runResponse.getRight().get().getRawPayload());
        } else {
            LOGGER.debug("using typed payload");
            answer = restResponseTranslator.from(answer, runResponse.getRight().get());
            Optional<byte[]> out = payloadToBytes(runResponse.getRight().get().getPayload());
            answer.setPayload(out);
        }

        return answer;
    }

    /**
     * Modularized so it can be overloaded for the dispatch error runner.
     *
     * @param body the optional request body
     * @return Optional<P> an optional for the expected payload
     * @throws DeserializationException if there was an issue serializing the body to the expected payload
     */
    protected Optional<P> to(Optional<byte[]> body) throws DeserializationException {
        return makeEntity(body);
    }

    protected void validate(Optional<P> entity) throws ValidateException {
        if (entity.isPresent()) {
            List<ValidateError> errors = validate.validate(entity.get());
            if (errors.size() > 0) {
                throw new ValidateException("Payload violated constraints", errors);
            }
        }
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

    protected Answer handleErrors(RestResponseError<S, U, P> error, Ask ask, Answer answer) throws HaltException {

        RestResponseError.ErrorType errorType = error.getErrorType();
        if (RestResponseError.ErrorType.HALT.equals(errorType)) {
            answer = restBtwnResponseTranslator.from(answer, error.getBtwnResponse());
            throw (HaltException) error.getCause();
        } else if (RestResponseError.ErrorType.BAD_REQUEST.equals(errorType)) {
            // a default bad request handler is always there.
            Optional<Answer> answerFromHandler = handle(StatusCode.BAD_REQUEST, error.getCause(), ask, answer);
            answer = answerFromHandler.get();
        } else if (RestResponseError.ErrorType.SERVER.equals(errorType)) {
            answer = handleServerError(error.getCause(), ask, answer);
        }

        return answer;
    }

    protected Optional<Answer> handle(StatusCode statusCode, Throwable cause, Ask ask, Answer answer) {
        Optional<Answer> answerFromHandler = Optional.empty();
        RestErrorHandler<U> errorHandler = errorHandlers.get(statusCode);
        if (Objects.nonNull(errorHandler)) {
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

    /**
     * This executes a route's before betweens, resource method, and the after betweens. If any exceptions occur
     * it will populate the right ivar of the RestReponseEither that is returned. An Either is used for the response
     * type in order to handle exceptions. The handlers should have access to the various request and response objects
     * which are parameterized types. Exceptions cannot have generic typed parameters.
     *
     * @param route the route to execute.
     * @param btwnRequest the request to pass before betweens.
     * @param btwnResponse the response to pass to before betweens.
     * @param entity the payload of the request.
     * @return A RestReponseEither, if left is present then it executed correctly. If right is present then an
     * error occurred and it should be handled.
     */
    protected RestReponseEither<S, U, P> executeResourceMethod(RestRoute<S, U, P> route, RestBtwnRequest<S, U> btwnRequest, RestBtwnResponse btwnResponse, Optional<P> entity) {

        RestReponseEither<S, U, P> responseEither = new RestReponseEither<>();
        RestResponseErrorBuilder<S, U, P> errorBuilder = new RestResponseErrorBuilder<>();

        RestRequest<U, P> requestForResource = null;
        RestResponse<P> responseForResource = null;
        RestResponse<P> resourceResponse = null;
        RestResponse<P> response = null;

        try {
            RestResource<U, P> resource = route.getRestResource();
            Method method = btwnRequest.getMethod();

            executeBetween(route.getBefore(), method, btwnRequest, btwnResponse);

            requestForResource = restRequestTranslator.to(btwnRequest, entity);
            responseForResource = restResponseTranslator.to(btwnResponse);

            resourceResponse = execute(method, resource, requestForResource, responseForResource);

            RestBtwnRequest<S, U> btwnRequestForAfter = restBtwnRequestTranslator.to(requestForResource);
            Optional<byte[]> resourceResponsePayload = payloadToBytes(resourceResponse.getPayload());
            RestBtwnResponse btwnResponseForAfter = restBtwnResponseTranslator.to(resourceResponse, resourceResponsePayload);

            executeBetween(route.getAfter(), method, btwnRequestForAfter, btwnResponseForAfter);

            // in case a after between modified the response.
            response = restResponseTranslator.to(btwnResponseForAfter);

            // sets the response payload. Decides which payload to use in case a after between modified the payload.
            setResponsePayload(resourceResponsePayload, btwnResponseForAfter.getPayload(), resourceResponse, response);

        } catch (HaltException e) {
            errorBuilder = errorBuilder
                .cause(e)
                .errorType(RestResponseError.ErrorType.HALT);
        } catch (DeserializationException e) {
            // it's more descriptive to use ServerException, other than e.
            ServerException cause = new ServerException("", e);
            errorBuilder = errorBuilder
                .cause(cause)
                .errorType(RestResponseError.ErrorType.SERVER);
        } catch (Throwable e) {
            // it's more descriptive to use ServerException, other than e.
            ServerException cause = new ServerException("", e);
            errorBuilder = errorBuilder
                .cause(cause)
                .errorType(RestResponseError.ErrorType.SERVER);;
        }

        RestResponseError<S, U, P> error = errorBuilder
                .btwnRequest(btwnRequest)
                .btwnResponse(btwnResponse)
                .requestForResource(requestForResource)
                .responseForResource(responseForResource)
                .resourceResponse(resourceResponse)
                .response(response)
                .build();

        responseEither.setLeft(Objects.isNull(error.getCause()) ? Optional.empty() : Optional.of(error));
        responseEither.setRight(Objects.isNull(error.getCause()) ? Optional.of(response) : Optional.empty());

        return responseEither;
    }

    protected RestResponse<P> execute(Method method, RestResource<U, P> resource, RestRequest<U, P> request, RestResponse<P> response) {

        RestResponse<P> resourceResponse = null;

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
        return resourceResponse;
    }

    protected void setResponsePayload(Optional<byte[]> payload, Optional<byte[]> afterPayload, RestResponse<P> resourceResponse, RestResponse<P> response) throws DeserializationException {
        // if a after between modified the response payload then it needs to re-hydrate.
        if (isPayloadDirty(payload, afterPayload)) {
            Optional<P> responseEntity;
            responseEntity = makeEntity(afterPayload);
            response.setPayload(responseEntity);
        } else {
            response.setPayload(resourceResponse.getPayload());
        }
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

    protected void executeBetween(List<RestBetween<S, U>> betweens, Method method, RestBtwnRequest<S, U> btwnRequest, RestBtwnResponse btwnResponse) throws HaltException {

        for(RestBetween<S, U> between: betweens) {
            try {
                between.process(method, btwnRequest, btwnResponse);
            } catch(HaltException e) {
                throw e;
            }
        }
    }

    protected Optional<byte[]> payloadToBytes(Optional<P> payload) {
        Optional<byte[]> out = Optional.empty();

        if (payload.isPresent()) {
            try {
                out = Optional.of(jsonTranslator.to(payload.get()));
            } catch (ToJsonException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return out;
    }

    public RestRoute<S, U, P> getRestRoute() {
        return restRoute;
    }

    // only used for tests to make sure it get built accurately.
    public Validate getValidate() {
        return validate;
    }
}

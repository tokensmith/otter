package org.rootservices.otter.dispatch.html;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.builder.ResponseErrorBuilder;
import org.rootservices.otter.dispatch.entity.either.ResponseEither;
import org.rootservices.otter.dispatch.entity.either.ResponseError;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.entity.between.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class RouteRun<S extends DefaultSession, U extends DefaultUser> implements RouteRunner {

    private Route<S, U> route;
    private RequestTranslator<S, U> requestTranslator;
    private AnswerTranslator<S> answerTranslator;
    private Map<StatusCode, Resource<S, U>> errorResources;

    public RouteRun(Route<S, U> route, RequestTranslator<S, U> requestTranslator, AnswerTranslator<S> answerTranslator, Map<StatusCode, Resource<S, U>> errorResources) {
        this.route = route;
        this.requestTranslator = requestTranslator;
        this.answerTranslator = answerTranslator;
        this.errorResources = errorResources;
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
        Request<S, U> request = requestTranslator.to(ask);
        Response<S> response = answerTranslator.from(answer);


        ResponseEither<S, U> runResponseEither = executeResourceMethod(route, request, response);

        if (runResponseEither.getRight().isPresent()) {
            answer = handleErrors(runResponseEither.getRight().get(), ask, answer);
        } else {
            answer = answerTranslator.to(runResponseEither.getLeft().get());
        }

        return answer;
    }

    protected Answer handleErrors(ResponseError<S, U> error, Ask ask, Answer answer) throws HaltException {
        ResponseError.ErrorType errorType = error.getErrorType();
        if (ResponseError.ErrorType.HALT.equals(errorType)) {
            answer = answerTranslator.to(answer, error.getResponse());
            throw (HaltException) error.getCause();
        } else if (ResponseError.ErrorType.SERVER.equals(errorType)) {
            Optional<Answer> answerFromErrorResource = handle(StatusCode.SERVER_ERROR, error.getCause(), ask, answer);
            if (! answerFromErrorResource.isPresent()) {
                // could not handle, no matching error resource.
                throw new HaltException(error.getCause().getMessage(), error.getCause());
            }
            answer = answerFromErrorResource.get();
        }
        return answer;
    };

    protected Optional<Answer> handle(StatusCode statusCode, Throwable cause, Ask ask, Answer answer) {
        Optional<Answer> answerFromErrorResource = Optional.empty();
        Resource<S, U> errorResource = errorResources.get(statusCode);

        if (errorResource != null) {
            Request<S, U> request = requestTranslator.to(ask);
            Response<S> response = answerTranslator.from(answer);
            Method method = request.getMethod();

            Response<S> responseFromErrorResource = null;
            if (method == Method.GET) {
                responseFromErrorResource = errorResource.get(request, response);
            } else if (method == Method.POST) {
                responseFromErrorResource = errorResource.post(request, response);
            } else if (method == Method.PUT) {
                responseFromErrorResource = errorResource.put(request, response);
            } else if (method == Method.PATCH) {
                responseFromErrorResource = errorResource.patch(request, response);
            } else if (method == Method.DELETE) {
                responseFromErrorResource = errorResource.delete(request, response);
            } else if (method == Method.CONNECT) {
                responseFromErrorResource = errorResource.connect(request, response);
            } else if (method == Method.OPTIONS) {
                responseFromErrorResource = errorResource.options(request, response);
            } else if (method == Method.TRACE) {
                responseFromErrorResource = errorResource.trace(request, response);
            } else if (method == Method.HEAD) {
                responseFromErrorResource = errorResource.head(request, response);
            }

            answerFromErrorResource = Optional.of(answerTranslator.to(answer, responseFromErrorResource));
        }

        return answerFromErrorResource;
    }

    /**
     * This executes a route's before betweens, resource method, and the after betweens. If any exceptions occur
     * it will populate the right ivar of the RestReponseEither that is returned. An Either is used for the response
     * type in order to handle exceptions. The handlers should have access to the various request and response objects
     * which are parameterized types. Exceptions cannot have generic typed parameters.
     *
     * @param route the route to execute.
     * @param request the request to process.
     * @param response the response to process.
     * @return A ReponseEither, if left is present then it executed correctly. If right is present then an
     * error occurred and it should be handled.
     */
    protected ResponseEither<S, U> executeResourceMethod(Route<S, U> route, Request<S, U> request, Response<S> response) {
        ResponseEither<S, U> responseEither = new ResponseEither<>();
        ResponseErrorBuilder<S, U> errorBuilder = new ResponseErrorBuilder<>();
        Resource<S, U> resource = route.getResource();
        Response<S> resourceResponse;
        Method method = request.getMethod();

        try {
            executeBetween(route.getBefore(), method, request, response);
            resourceResponse = execute(method, resource, request, response);
            executeBetween(route.getAfter(), method, request, resourceResponse);
        } catch (HaltException e) {
            errorBuilder = errorBuilder
                .cause(e)
                .errorType(ResponseError.ErrorType.HALT);
        } catch (Throwable e) {
            errorBuilder = errorBuilder
                .cause(e)
                .errorType(ResponseError.ErrorType.SERVER);
        }

        ResponseError<S, U> error = errorBuilder
            .request(request)
            .response(response)
            .build();

        responseEither.setRight(error.getCause() == null ? Optional.empty() : Optional.of(error));
        responseEither.setLeft(error.getCause() == null ? Optional.of(response) : Optional.empty());

        return responseEither;
    }

    protected Response<S> execute(Method method, Resource<S, U> resource, Request<S, U> request, Response<S> response) {

        Response<S> resourceResponse = null;

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

    protected void executeBetween(List<Between<S, U>> betweens, Method method, Request<S, U> request, Response<S> response) throws HaltException {
        for(Between<S, U> between: betweens) {
            try {
                between.process(method, request, response);
            } catch(HaltException e) {
                throw e;
            }
        }
    }

    public Route<S, U> getRoute() {
        return route;
    }

    public RequestTranslator<S, U> getRequestTranslator() {
        return requestTranslator;
    }

    public AnswerTranslator<S> getAnswerTranslator() {
        return answerTranslator;
    }
}

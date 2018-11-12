package org.rootservices.otter.dispatch;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.entity.between.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;

import java.util.List;

public class RouteRun<S extends DefaultSession, U extends DefaultUser> implements RouteRunner {

    private Route<S, U> route;
    private RequestTranslator<S, U> requestTranslator;
    private AnswerTranslator<S> answerTranslator;

    public RouteRun(Route<S, U> route, RequestTranslator<S, U> requestTranslator, AnswerTranslator<S> answerTranslator) {
        this.route = route;
        this.requestTranslator = requestTranslator;
        this.answerTranslator = answerTranslator;
    }

    @Override
    public Answer run(Ask ask, Answer answer) throws HaltException {
        Request<S, U> request = requestTranslator.to(ask);
        Response<S> response = answerTranslator.from(answer);

        Response<S> runResponse;
        try {
            runResponse = executeResourceMethod(route, request, response);
        } catch (HaltException e) {
            // response may have been updated in a between
            answer = answerTranslator.to(answer, response);
            throw e;
        }

        return answerTranslator.to(runResponse);
    }

    protected Response<S> executeResourceMethod(Route<S, U> route, Request<S, U> request, Response<S> response) throws HaltException {
        Resource<S, U> resource = route.getResource();
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

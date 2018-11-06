package org.rootservices.otter.dispatch;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.*;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;

public class HtmlRouteRun<S extends DefaultSession, U extends DefaultUser, P extends Translatable> implements RouteRunner {

    private Route<S, U, P> route;
    private RequestTranslator<S, U, P> requestTranslator;
    private AnswerTranslator<S> answerTranslator;

    public HtmlRouteRun(Route<S, U, P> route, RequestTranslator<S, U, P> requestTranslator, AnswerTranslator<S> answerTranslator) {
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
            // response may have been updated in a between
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

    protected void executeBetween(List<Between<S, U, P>> betweens, Method method, Request<S, U, P> request, Response<S> response) throws HaltException {
        for(Between<S, U, P> between: betweens) {
            try {
                between.process(method, request, response);
            } catch(HaltException e) {
                throw e;
            }
        }
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

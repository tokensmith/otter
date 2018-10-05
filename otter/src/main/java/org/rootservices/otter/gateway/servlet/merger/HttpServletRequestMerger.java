package org.rootservices.otter.gateway.servlet.merger;


import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;
import org.rootservices.otter.router.entity.io.Answer;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class HttpServletRequestMerger {
    private static String PRESENTER_ATTR = "presenter";

    public HttpServletRequest merge(HttpServletRequest containerRequest, Response response) {

        // presenter
        if(response.getPresenter().isPresent()) {
            containerRequest.setAttribute(PRESENTER_ATTR, response.getPresenter().get());
        }

        return containerRequest;
    }

    public HttpServletRequest mergeForAnswer(HttpServletRequest containerRequest, Answer answer) {

        // presenter
        if(answer.getPresenter().isPresent()) {
            containerRequest.setAttribute(PRESENTER_ATTR, answer.getPresenter().get());
        }

        return containerRequest;
    }

    public String getPresenterAttr() {
        return PRESENTER_ATTR;
    }
}

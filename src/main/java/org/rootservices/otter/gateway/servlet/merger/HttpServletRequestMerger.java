package org.rootservices.otter.gateway.servlet.merger;


import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class HttpServletRequestMerger {
    private static String PRESENTER_ATTR = "presenter";

    public HttpServletRequest merge(AsyncContext ac, HttpServletRequest containerRequest, Response response) throws IOException, ServletException {

        // presenter
        if(response.getPresenter().isPresent()) {
            containerRequest.setAttribute(PRESENTER_ATTR, response.getPresenter().get());
        }

        // template
        if (response.getTemplate().isPresent()) {
            ac.dispatch(response.getTemplate().get());
        }
        return containerRequest;
    }

    public String getPresenterAttr() {
        return PRESENTER_ATTR;
    }
}

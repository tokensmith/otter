package net.tokensmith.otter.gateway.servlet.merger;


import net.tokensmith.otter.router.entity.io.Answer;
import javax.servlet.http.HttpServletRequest;


public class HttpServletRequestMerger {
    private static String PRESENTER_ATTR = "presenter";

    public HttpServletRequest merge(HttpServletRequest containerRequest, Answer answer) {

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

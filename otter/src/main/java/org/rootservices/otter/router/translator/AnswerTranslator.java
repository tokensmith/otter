package org.rootservices.otter.router.translator;

import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.io.Answer;

public class AnswerTranslator<S> {

    public Answer to(Response<S> from) {
        Answer to = new Answer();
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(from.getPayload());
        to.setTemplate(from.getTemplate());
        to.setPresenter(from.getPresenter());

        return to;
    }
}

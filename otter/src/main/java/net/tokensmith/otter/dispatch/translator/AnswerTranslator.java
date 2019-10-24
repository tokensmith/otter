package net.tokensmith.otter.dispatch.translator;

import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.io.Answer;

import java.util.Optional;

public class AnswerTranslator<S> {

    public Answer to(Response<S> from) {
        Answer to = new Answer();
        return to(to, from);
    }

    public Answer to(Answer to, Response<S> from) {
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(from.getPayload());
        to.setTemplate(from.getTemplate());
        to.setPresenter(from.getPresenter());

        return to;
    }

    public Response<S> from(Answer to) {
        Response<S> from = new Response<>();
        from.setStatusCode(to.getStatusCode());
        from.setHeaders(to.getHeaders());
        from.setCookies(to.getCookies());
        from.setPayload(to.getPayload());
        from.setTemplate(to.getTemplate());
        from.setPresenter(to.getPresenter());
        from.setSession(Optional.empty());

        return from;
    }
}

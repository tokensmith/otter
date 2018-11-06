package org.rootservices.otter.dispatch.translator;

import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.router.entity.io.Ask;

import java.util.Optional;

public class RequestTranslator<S, U, P> {

    public Request<S, U, P> to(Ask from) {
        Request<S, U, P> to = new Request<>();

        to.setMatcher(from.getMatcher());
        to.setMethod(from.getMethod());
        to.setPathWithParams(from.getPathWithParams());
        to.setContentType(from.getContentType());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setBody(from.getBody());
        to.setQueryParams(from.getQueryParams());
        to.setFormData(from.getFormData());
        to.setBody(from.getBody());
        to.setCsrfChallenge(from.getCsrfChallenge());
        to.setIpAddress(from.getIpAddress());
        to.setSession(Optional.empty());
        to.setUser(Optional.empty());

        return to;
    }
}

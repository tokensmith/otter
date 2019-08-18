package org.rootservices.otter.dispatch.translator;

import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.router.entity.io.Ask;

import java.util.Optional;

public class RequestTranslator<S, U> {

    public Request<S, U> to(Ask from) {
        Request<S, U> to = new Request<>();

        to.setMatcher(from.getMatcher());
        to.setPossibleContentTypes(from.getPossibleContentTypes());
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

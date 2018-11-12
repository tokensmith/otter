package org.rootservices.otter.dispatch.translator.rest;

import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.router.entity.io.Ask;

import java.util.Optional;

public class RestRequestTranslator<U, P> {

    public RestRequest<U, P> to(Ask from) {
        RestRequest<U, P> to = new RestRequest<U, P>();

        to.setMatcher(from.getMatcher());
        to.setMethod(from.getMethod());
        to.setPathWithParams(from.getPathWithParams());
        to.setContentType(from.getContentType());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setQueryParams(from.getQueryParams());
        to.setFormData(from.getFormData());
        to.setBody(from.getBody());
        to.setIpAddress(from.getIpAddress());
        to.setUser(Optional.empty());
        to.setPayload(Optional.empty());

        return to;
    }
}

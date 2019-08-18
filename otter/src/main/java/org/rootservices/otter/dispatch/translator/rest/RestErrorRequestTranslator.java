package org.rootservices.otter.dispatch.translator.rest;

import org.rootservices.otter.dispatch.entity.RestBtwnRequest;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.router.entity.io.Ask;

import java.util.Optional;

public class RestErrorRequestTranslator<U> {

    public RestErrorRequest<U> to(Ask from) {
        RestErrorRequest<U> to = new RestErrorRequest<U>();

        to.setMatcher(from.getMatcher());
        to.setPossibleContentTypes(from.getPossibleContentTypes());
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
        to.setBody(from.getBody());

        return to;
    }

    public RestErrorRequest<U> to(RestBtwnRequest<U> from) {
        RestErrorRequest<U> to = new RestErrorRequest<U>();

        to.setMatcher(from.getMatcher());
        to.setPossibleContentTypes(from.getPossibleContentTypes());
        to.setMethod(from.getMethod());
        to.setPathWithParams(from.getPathWithParams());
        to.setContentType(from.getContentType());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setQueryParams(from.getQueryParams());
        to.setFormData(from.getFormData());
        to.setBody(from.getBody());
        to.setIpAddress(from.getIpAddress());
        to.setBody(from.getBody());
        to.setUser(from.getUser());

        return to;
    }
}

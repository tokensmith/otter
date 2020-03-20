package net.tokensmith.otter.dispatch.translator.rest;


import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.router.entity.io.Ask;

import java.util.Optional;

public class RestBtwnRequestTranslator<S, U, P> {

    // inbound
    public RestBtwnRequest<S, U> to(Ask from) {
        RestBtwnRequest<S, U> to = new RestBtwnRequest<S, U>();

        to.setMatcher(from.getMatcher());
        to.setPossibleContentTypes(from.getPossibleContentTypes());
        to.setPossibleAccepts(from.getPossibleAccepts());
        to.setMethod(from.getMethod());
        to.setPathWithParams(from.getPathWithParams());
        to.setContentType(from.getContentType());
        to.setAccept(from.getAccept());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setQueryParams(from.getQueryParams());
        to.setFormData(from.getFormData());
        to.setBody(from.getBody());
        to.setIpAddress(from.getIpAddress());
        to.setUser(Optional.empty());
        to.setBody(from.getBody());
        to.setSession(Optional.empty());

        return to;
    }

    // outbound
    public RestBtwnRequest<S, U> to(RestRequest<U, P> from) {
        RestBtwnRequest<S, U> to = new RestBtwnRequest<S, U>();

        to.setMatcher(from.getMatcher());
        to.setPossibleContentTypes(from.getPossibleContentTypes());
        to.setPossibleAccepts(from.getPossibleAccepts());
        to.setMethod(from.getMethod());
        to.setPathWithParams(from.getPathWithParams());
        to.setContentType(from.getContentType());
        to.setAccept(from.getAccept());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setQueryParams(from.getQueryParams());
        to.setFormData(from.getFormData());
        to.setBody(from.getBody());
        to.setIpAddress(from.getIpAddress());
        to.setUser(from.getUser());
        to.setBody(from.getBody());
        to.setSession(Optional.empty());

        return to;
    }
}

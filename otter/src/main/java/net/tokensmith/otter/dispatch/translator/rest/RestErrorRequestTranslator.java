package net.tokensmith.otter.dispatch.translator.rest;

import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestErrorRequest;
import net.tokensmith.otter.router.entity.io.Ask;

import java.util.Optional;

public class RestErrorRequestTranslator<S, U> {

    public RestErrorRequest<U> to(Ask from) {
        RestErrorRequest<U> to = new RestErrorRequest<U>();

        to.setMatcher(from.getMatcher());
        to.setPossibleContentTypes(from.getPossibleContentTypes());
        to.setPossibleAccept(from.getPossibleAccepts());
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

        return to;
    }

    public RestErrorRequest<U> to(RestBtwnRequest<S, U> from) {
        RestErrorRequest<U> to = new RestErrorRequest<U>();

        to.setMatcher(from.getMatcher());
        to.setPossibleContentTypes(from.getPossibleContentTypes());
        to.setPossibleAccept(from.getPossibleAccepts());
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
        to.setBody(from.getBody());
        to.setUser(from.getUser());

        return to;
    }
}

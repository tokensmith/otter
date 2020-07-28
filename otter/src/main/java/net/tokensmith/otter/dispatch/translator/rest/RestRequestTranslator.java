package net.tokensmith.otter.dispatch.translator.rest;

import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestErrorRequest;
import net.tokensmith.otter.router.entity.io.Ask;

import java.util.Objects;
import java.util.Optional;

public class RestRequestTranslator<S, U, P> {

    // inbound - when betweens are not present
    public RestRequest<U, P> to(Ask from) {
        RestRequest<U, P> to = new RestRequest<U, P>();

        to.setMatcher(from.getMatcher());
        to.setPossibleContentTypes(from.getPossibleContentTypes());
        to.setPossibleAccepts(from.getPossibleAccepts());
        to.setMethod(from.getMethod());
        to.setScheme(from.getScheme());
        to.setAuthority(from.getAuthority());
        to.setPort(from.getPort());
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
        to.setPayload(Optional.empty());
        to.setCause(Optional.empty());

        return to;
    }

    // inbound - when betweens are present
    public RestRequest<U, P> to(RestBtwnRequest<S, U> from, Optional<P> entity) {
        RestRequest<U, P> to = new RestRequest<U, P>();

        to.setMatcher(from.getMatcher());
        to.setScheme(from.getScheme());
        to.setAuthority(from.getAuthority());
        to.setPort(from.getPort());
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
        to.setPayload(entity);
        to.setUser(from.getUser());
        to.setCause(Optional.empty());

        return to;
    }

    // error scenarios with JsonErrorHandler
    public RestRequest<U, P> to(RestErrorRequest<U> from, Throwable cause) {
        RestRequest<U, P> to = new RestRequest<U, P>();

        to.setMatcher(from.getMatcher());
        to.setCause(
            Objects.nonNull(cause) ? Optional.of(cause) : Optional.empty()
        );
        to.setScheme(from.getScheme());
        to.setAuthority(from.getAuthority());
        to.setPort(from.getPort());
        to.setPossibleContentTypes(from.getPossibleContentTypes());
        to.setPossibleAccepts(from.getPossibleAccept());
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
        to.setPayload(Optional.empty());
        to.setCause(Optional.of(cause));

        return to;
    }
}

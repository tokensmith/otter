package org.rootservices.otter.dispatch.translator.rest;

import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.dispatch.entity.RestErrorResponse;
import org.rootservices.otter.router.entity.io.Answer;

import java.util.Optional;

public class RestResponseTranslator<P> {

    public Answer from(RestResponse<P> from) {
        Answer to = new Answer();
        return from(to, from);
    }

    public Answer from(Answer to, RestResponse<P> from) {
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(Optional.empty());

        return to;
    }

    // inbound - when betweens are not present
    public RestResponse<P> to(Answer from) {
        RestResponse<P> to = new RestResponse<>();
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(Optional.empty());

        return to;
    }

    // outbound - when betweens are present
    public RestResponse<P> to(RestBtwnResponse from) {
        RestResponse<P> to = new RestResponse<>();
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(Optional.empty());
        to.setRawPayload(from.getRawPayload());

        return to;
    }

    public RestResponse<P> to(RestErrorResponse from) {
        RestResponse<P> to = new RestResponse<>();
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(Optional.empty());

        return to;
    }

}

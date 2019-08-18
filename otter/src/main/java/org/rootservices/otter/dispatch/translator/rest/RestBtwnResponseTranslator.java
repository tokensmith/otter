package org.rootservices.otter.dispatch.translator.rest;

import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.router.entity.io.Answer;

import java.util.Optional;

public class RestBtwnResponseTranslator<P> {

    // exceptional state when between throws a HaltException.
    public Answer from(Answer to, RestBtwnResponse from) {
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(from.getPayload());

        return to;
    }

    // inbound
    public RestBtwnResponse to(Answer from) {
        RestBtwnResponse to = new RestBtwnResponse();
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(from.getPayload());

        return to;
    }

    // outbound
    public RestBtwnResponse to(RestResponse<P> from, Optional<byte[]> fromPayload) {
        RestBtwnResponse to = new RestBtwnResponse();
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(fromPayload);

        return to;
    }
}

package org.rootservices.otter.dispatch.translator.rest;

import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.dispatch.entity.RestErrorResponse;
import org.rootservices.otter.router.entity.io.Answer;

import java.util.Optional;

public class RestErrorResponseTranslator {

    public RestErrorResponse to(Answer from) {
        RestErrorResponse to = new RestErrorResponse();
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(Optional.empty());

        return to;
    }


    public RestErrorResponse to(RestBtwnResponse from) {
        RestErrorResponse to = new RestErrorResponse();
        to.setStatusCode(from.getStatusCode());
        to.setHeaders(from.getHeaders());
        to.setCookies(from.getCookies());
        to.setPayload(Optional.empty());

        return to;
    }

}

package net.tokensmith.otter.dispatch.translator.rest;

import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.dispatch.entity.RestErrorResponse;
import net.tokensmith.otter.router.entity.io.Answer;

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

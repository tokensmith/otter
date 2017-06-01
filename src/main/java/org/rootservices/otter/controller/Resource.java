package org.rootservices.otter.controller;


import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Resource {

    public Response get(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    public Response post(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    public Response put(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    public Response delete(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    public Response connect(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    public Response options(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    public Response trace(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    public Response patch(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    public Response head(Request request) {
        ResponseBuilder responseBuilder = responseBuilder(request.getCookies());
        return responseBuilder.notImplemented().build();
    }

    protected ResponseBuilder responseBuilder(Map<String, Cookie> cookies) {
        ResponseBuilder responseBuilder = new ResponseBuilder();

        return responseBuilder
                .setHeaders(new HashMap<>())
                .setCookies(cookies)
                .setBody(Optional.empty())
                .setPresenter(Optional.empty())
                .setTemplate(Optional.empty());
    }
}

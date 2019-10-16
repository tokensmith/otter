package org.rootservices.otter.dispatch.json;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.request.RestRequest;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.dispatch.entity.RestErrorResponse;
import org.rootservices.otter.dispatch.translator.RestErrorHandler;
import org.rootservices.otter.dispatch.translator.rest.RestRequestTranslator;
import org.rootservices.otter.dispatch.translator.rest.RestResponseTranslator;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.ToJsonException;

import java.util.Optional;


public class JsonErrorHandler<U extends DefaultUser, P> implements RestErrorHandler<U> {
    protected static Logger LOGGER = LoggerFactory.getLogger(JsonErrorHandler.class);
    private JsonTranslator<P> jsonTranslator;
    private RestResource<U, P> resource;
    private RestRequestTranslator<U, P> restRequestTranslator;
    private RestResponseTranslator<P> restResponseTranslator;


    public JsonErrorHandler(JsonTranslator<P> jsonTranslator, RestResource<U, P> resource, RestRequestTranslator<U, P> restRequestTranslator, RestResponseTranslator<P> restResponseTranslator) {
        this.jsonTranslator = jsonTranslator;
        this.resource = resource;
        this.restRequestTranslator = restRequestTranslator;
        this.restResponseTranslator = restResponseTranslator;
    }

    @Override
    public Answer run(RestErrorRequest<U> request, RestErrorResponse response, Throwable cause) {
        RestRequest<U, P> requestToResource = restRequestTranslator.to(request, cause);
        RestResponse<P> responseToResource = restResponseTranslator.to(response);
        RestResponse<P> responseFromResource = execute(resource, requestToResource, responseToResource, cause);

        Optional<byte[]> out = Optional.empty();
        Answer answer = restResponseTranslator.from(responseFromResource);
        if(responseFromResource.getPayload().isPresent()) {
            out = payloadToBytes(responseFromResource.getPayload());
            answer.setPayload(out);
        } else if (responseFromResource.getRawPayload().isPresent()) {
            out = responseFromResource.getRawPayload();
        }
        answer.setPayload(out);
        return answer;
    }

    protected RestResponse<P> execute(RestResource<U,P> resource, RestRequest<U, P> request, RestResponse<P> response, Throwable cause) {
        Method method = request.getMethod();
        RestResponse<P> resourceResponse = null;

        if (method == Method.GET) {
            resourceResponse = resource.get(request, response);
        } else if (method == Method.POST) {
            resourceResponse = resource.post(request, response);
        } else if (method == Method.PUT) {
            resourceResponse = resource.put(request, response);
        } else if (method == Method.PATCH) {
            resourceResponse = resource.patch(request, response);
        } else if (method == Method.DELETE) {
            resourceResponse = resource.delete(request, response);
        } else if (method == Method.CONNECT) {
            resourceResponse = resource.connect(request, response);
        } else if (method == Method.OPTIONS) {
            resourceResponse = resource.options(request, response);
        } else if (method == Method.TRACE) {
            resourceResponse = resource.trace(request, response);
        } else if (method == Method.HEAD) {
            resourceResponse = resource.head(request, response);
        }

        return resourceResponse;
    }

    protected Optional<byte[]> payloadToBytes(Optional<P> payload) {
        Optional<byte[]> out = Optional.empty();
        if (payload.isPresent()) {
            try {
                out = Optional.of(jsonTranslator.to(payload.get()));
            } catch (ToJsonException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return out;
    }
}

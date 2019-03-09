package org.rootservices.otter.dispatch;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.RestErrorResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.response.RestResponse;
import org.rootservices.otter.dispatch.entity.RestErrorRequest;
import org.rootservices.otter.dispatch.entity.RestErrorResponse;
import org.rootservices.otter.dispatch.translator.RestErrorHandler;
import org.rootservices.otter.dispatch.translator.rest.RestResponseTranslator;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.ToJsonException;

import java.util.Optional;


public class JsonErrorHandler<U extends DefaultUser, P> implements RestErrorHandler<U> {
    protected static Logger logger = LogManager.getLogger(JsonErrorHandler.class);
    private JsonTranslator<P> jsonTranslator;
    private RestErrorResource<U, P> resource;
    private RestResponseTranslator<P> restResponseTranslator;

    public JsonErrorHandler(JsonTranslator<P> jsonTranslator, RestErrorResource<U, P> resource, RestResponseTranslator<P> restResponseTranslator) {
        this.jsonTranslator = jsonTranslator;
        this.resource = resource;
        this.restResponseTranslator = restResponseTranslator;
    }

    @Override
    public Answer run(RestErrorRequest<U> request, RestErrorResponse response, Throwable cause) {
        RestResponse<P> responseToResource = restResponseTranslator.to(response);
        RestResponse<P> responseFromResource = execute(resource, request, responseToResource, cause);

        // response entity marshalling
        Answer answer = restResponseTranslator.from(responseFromResource);
        Optional<byte[]> out = payloadToBytes(responseFromResource.getPayload());
        answer.setPayload(out);

        return answer;
    }

    protected RestResponse<P> execute(RestErrorResource<U,P> resource, RestErrorRequest<U> request, RestResponse<P> response, Throwable cause) {
        Method method = request.getMethod();
        RestResponse<P> resourceResponse = null;

        if (method == Method.GET) {
            resourceResponse = resource.get(request, response, cause);
        } else if (method == Method.POST) {
            resourceResponse = resource.post(request, response, cause);
        } else if (method == Method.PUT) {
            resourceResponse = resource.put(request, response, cause);
        } else if (method == Method.PATCH) {
            resourceResponse = resource.patch(request, response, cause);
        } else if (method == Method.DELETE) {
            resourceResponse = resource.delete(request, response, cause);
        } else if (method == Method.CONNECT) {
            resourceResponse = resource.connect(request, response, cause);
        } else if (method == Method.OPTIONS) {
            resourceResponse = resource.options(request, response, cause);
        } else if (method == Method.TRACE) {
            resourceResponse = resource.trace(request, response, cause);
        } else if (method == Method.HEAD) {
            resourceResponse = resource.head(request, response, cause);
        }

        return resourceResponse;
    }

    protected Optional<byte[]> payloadToBytes(Optional<P> payload) {
        Optional<byte[]> out = Optional.empty();
        try {
            out = Optional.of(jsonTranslator.to(payload));
        } catch (ToJsonException e) {
            logger.error(e.getMessage(), e);
        }

        return out;
    }
}

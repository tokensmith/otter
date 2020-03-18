package net.tokensmith.otter.dispatch.json;


import net.tokensmith.otter.controller.entity.DefaultSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.dispatch.entity.RestErrorRequest;
import net.tokensmith.otter.dispatch.entity.RestErrorResponse;
import net.tokensmith.otter.dispatch.translator.RestErrorHandler;
import net.tokensmith.otter.dispatch.translator.rest.RestRequestTranslator;
import net.tokensmith.otter.dispatch.translator.rest.RestResponseTranslator;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.translator.JsonTranslator;
import net.tokensmith.otter.translator.exception.ToJsonException;

import java.util.Optional;


public class JsonErrorHandler<S extends DefaultSession, U extends DefaultUser, P> implements RestErrorHandler<U> {
    protected static Logger LOGGER = LoggerFactory.getLogger(JsonErrorHandler.class);
    private JsonTranslator<P> jsonTranslator;
    private RestResource<U, P> resource;
    private RestRequestTranslator<S, U, P> restRequestTranslator;
    private RestResponseTranslator<P> restResponseTranslator;


    public JsonErrorHandler(JsonTranslator<P> jsonTranslator, RestResource<U, P> resource, RestRequestTranslator<S, U, P> restRequestTranslator, RestResponseTranslator<P> restResponseTranslator) {
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

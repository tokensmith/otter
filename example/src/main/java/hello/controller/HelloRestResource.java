package hello.controller;


import hello.model.Hello;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.ToJsonException;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

public class HelloRestResource extends RestResource<Hello> {
    public static String URL = "/rest/hello";

    public HelloRestResource(JsonTranslator<Hello> translator) {
        super(translator);
    }

    @Override
    public Response get(Request request, Response response) {
        response.setStatusCode(StatusCode.OK);

        Hello hello = new Hello("Hello World");
        Optional<ByteArrayOutputStream> payload = Optional.empty();

        try {
            payload = Optional.of(translator.to(hello));
        } catch (ToJsonException e) {
            response.setStatusCode(StatusCode.SERVER_ERROR);
        }

        response.setPayload(payload);
        return response;
    }

    @Override
    public Response post(Request request, Response response, Hello entity) {
        response.setStatusCode(StatusCode.CREATED);

        Optional<ByteArrayOutputStream> payload = Optional.empty();

        try {
            payload = Optional.of(translator.to(entity));
        } catch (ToJsonException e) {
            response.setStatusCode(StatusCode.SERVER_ERROR);
        }

        response.setPayload(payload);
        return response;
    }
}

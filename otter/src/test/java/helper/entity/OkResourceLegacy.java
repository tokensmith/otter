package helper.entity;


import org.rootservices.otter.controller.LegacyRestResource;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.translator.JsonTranslator;

public class OkResourceLegacy extends LegacyRestResource<DummyPayload, DummySession, DummyUser> {

    public OkResourceLegacy(JsonTranslator<DummyPayload> translator) {
        super(translator);
    }

    @Override
    public Response<DummySession> get(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    protected Response<DummySession> post(Request<DummySession, DummyUser> request, Response<DummySession> response, DummyPayload entity) {
        response.setStatusCode(StatusCode.CREATED);
        return response;
    }

    @Override
    protected Response<DummySession> put(Request<DummySession, DummyUser> request, Response<DummySession> response, DummyPayload entity) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    protected Response<DummySession> patch(Request<DummySession, DummyUser> request, Response<DummySession> response, DummyPayload entity) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> delete(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> connect(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> options(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> trace(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }

    @Override
    public Response<DummySession> head(Request<DummySession, DummyUser> request, Response<DummySession> response) {
        response.setStatusCode(StatusCode.OK);
        return response;
    }
}

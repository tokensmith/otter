package org.rootservices.hello.controller.html;

import org.rootservices.hello.security.TokenSession;
import org.rootservices.hello.security.User;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;

import java.util.Optional;

public class MediaTypeResource extends Resource<TokenSession, User> {
    private static Optional<String> JSP_PATH = Optional.of("/WEB-INF/jsp/415.jsp");

    @Override
    public Response<TokenSession> get(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> put(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> delete(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> connect(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> options(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> trace(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> patch(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> head(Request<TokenSession, User> request, Response<TokenSession> response) {
        response.setStatusCode(StatusCode.UNSUPPORTED_MEDIA_TYPE);
        response.setTemplate(JSP_PATH);
        return response;
    }
}

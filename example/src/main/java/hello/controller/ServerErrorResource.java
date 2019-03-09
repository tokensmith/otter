package hello.controller;

import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.otter.controller.ErrorResource;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.Optional;

public class ServerErrorResource extends ErrorResource<TokenSession, DefaultUser> {
    private static Optional<String> JSP_PATH = Optional.of("/WEB-INF/jsp/500.jsp");

    @Override
    public Response<TokenSession> get(Request<TokenSession, DefaultUser> request, Response<TokenSession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, DefaultUser> request, Response<TokenSession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> put(Request<TokenSession, DefaultUser> request, Response<TokenSession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> delete(Request<TokenSession, DefaultUser> request, Response<TokenSession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> connect(Request<TokenSession, DefaultUser> request, Response<TokenSession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> options(Request<TokenSession, DefaultUser> request, Response<TokenSession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> trace(Request<TokenSession, DefaultUser> request, Response<TokenSession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> patch(Request<TokenSession, DefaultUser> request, Response<TokenSession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(JSP_PATH);
        return response;
    }

    @Override
    public Response<TokenSession> head(Request<TokenSession, DefaultUser> request, Response<TokenSession> response, Throwable cause) {
        response.setStatusCode(StatusCode.SERVER_ERROR);
        response.setTemplate(JSP_PATH);
        return response;
    }
}

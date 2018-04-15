package integration.app.hello.controller;


import integration.app.hello.controller.presenter.LoginPresenter;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.Optional;
import java.util.UUID;

public class LoginResource extends Resource {
    public static String URL = "/login";
    private static String JSP_PATH = "/WEB-INF/jsp/login.jsp";

    @Override
    public Response get(Request request, Response response) {
        LoginPresenter presenter = new LoginPresenter("", request.getCsrfChallenge().get());
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_PATH));
        return response;
    }

    @Override
    public Response post(Request request, Response response) {
        LoginPresenter presenter = new LoginPresenter("", request.getCsrfChallenge().get());
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_PATH));

        // example of how to set a session for a user.
        TokenSession tokenSession = new TokenSession();
        tokenSession.setAccessToken(UUID.randomUUID());
        response.setSession(Optional.of(tokenSession));
        return response;
    }
}

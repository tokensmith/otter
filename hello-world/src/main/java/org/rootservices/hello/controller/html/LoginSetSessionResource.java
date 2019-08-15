package org.rootservices.hello.controller.html;


import org.rootservices.hello.controller.html.presenter.LoginPresenter;
import org.rootservices.hello.security.TokenSession;
import org.rootservices.hello.security.User;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.Optional;
import java.util.UUID;

/**
 * Example of a resource that depends on a CSRF before and a Encrypted Session after.
 */
public class LoginSetSessionResource extends Resource<TokenSession, User> {
    public static String URL = "/login-set-session";
    private static String JSP_PATH = "/WEB-INF/jsp/login-set-session.jsp";

    @Override
    public Response<TokenSession> get(Request<TokenSession, User> request, Response<TokenSession> response) {
        LoginPresenter presenter = new LoginPresenter("", request.getCsrfChallenge().get());
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_PATH));
        return response;
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, User> request, Response<TokenSession> response) {
        LoginPresenter presenter = new LoginPresenter("", request.getCsrfChallenge().get());
        response.setPresenter(Optional.of(presenter));
        response.setStatusCode(StatusCode.OK);
        response.setTemplate(Optional.of(JSP_PATH));

        // set up the session..
        TokenSession tokenSession = new TokenSession(UUID.fromString("2cf081ed-aa7c-4141-b634-01fb56bc96bb"));
        response.setSession(Optional.of(tokenSession));
        return response;
    }
}

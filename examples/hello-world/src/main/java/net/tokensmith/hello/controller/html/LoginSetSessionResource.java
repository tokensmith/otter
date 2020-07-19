package net.tokensmith.hello.controller.html;


import net.tokensmith.hello.controller.html.presenter.LoginPresenter;
import net.tokensmith.hello.security.TokenSession;
import net.tokensmith.hello.security.User;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;

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

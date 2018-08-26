package hello.controller;


import hello.controller.presenter.LoginPresenter;
import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.Optional;
import java.util.UUID;

/**
 * Example of a resource that depends on a CSRF and Session before and a Encrypted Session after.
 */
public class LoginSessionResource extends Resource<TokenSession, User> {
    public static String URL = "/login-with-session";
    private static String JSP_PATH = "/WEB-INF/jsp/login-with-session.jsp";

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

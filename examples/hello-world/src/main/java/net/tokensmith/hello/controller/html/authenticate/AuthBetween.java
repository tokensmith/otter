package net.tokensmith.hello.controller.html.authenticate;

import net.tokensmith.hello.security.TokenSession;
import net.tokensmith.hello.security.User;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.exception.HaltException;

import java.util.Optional;
import java.util.UUID;

/**
 * A Simple example of setting the user in a Authentication Between.
 *
 * In a real application it would use data in the Session to fetch the user from a identity server or database or
 * wherever you want. If authentication fails then throw a HaltException. That will stop the request from reaching its
 * intended Resource.
 */
public class AuthBetween implements Between<TokenSession, User> {
    @Override
    public void process(Method method, Request<TokenSession, User> request, Response<TokenSession> response) throws HaltException {
        User webUser = new User(UUID.randomUUID(), "Obi-Wan", "Kenobi");
        request.setUser(Optional.of(webUser));
    }
}

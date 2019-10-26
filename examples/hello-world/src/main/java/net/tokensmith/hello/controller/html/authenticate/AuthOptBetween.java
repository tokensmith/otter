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
 * This is only an example of how to optionally authenticate a user. For simplicity this will always assign the user.
 * In a real application if authenticate fails, then set the user to empty.
 */
public class AuthOptBetween implements Between<TokenSession, User> {
    @Override
    public void process(Method method, Request<TokenSession, User> request, Response<TokenSession> response) throws HaltException {
        User webUser = new User(UUID.randomUUID(), "Obi-Wan", "Kenobi");
        request.setUser(Optional.of(webUser));
    }
}

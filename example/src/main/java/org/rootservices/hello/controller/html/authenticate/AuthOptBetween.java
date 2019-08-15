package org.rootservices.hello.controller.html.authenticate;

import org.rootservices.hello.security.TokenSession;
import org.rootservices.hello.security.User;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.Between;
import org.rootservices.otter.router.exception.HaltException;

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

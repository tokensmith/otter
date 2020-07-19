package net.tokensmith.hello.controller.api.between;

import net.tokensmith.hello.controller.api.model.ApiUser;
import net.tokensmith.hello.security.TokenSession;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;

import java.util.Optional;
import java.util.UUID;

/**
 * A Simple example of setting the user in a Authentication Between.
 *
 * In a real application use the authentication header to exchange a token for the user
 * Throwing a halt exception will stop the request from continuing to the its desired resource.
 */
public class AuthRestBetween implements RestBetween<TokenSession, ApiUser> {
    @Override
    public void process(Method method, RestBtwnRequest<TokenSession, ApiUser> request, RestBtwnResponse response) throws HaltException {
        ApiUser apiUser = new ApiUser(UUID.randomUUID(), "Obi-Wan", "Kenobi");
        request.setUser(Optional.of(apiUser));
    }
}

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
 * A Simple example of setting the user in a Authentication Between from a session.
 *
 * In a real application use the value from the session to exchange for the user
 * Throwing a halt exception will stop the request from continuing to the its desired resource.
 */
public class AuthSessionRestBetween implements RestBetween<TokenSession, ApiUser> {
    @Override
    public void process(Method method, RestBtwnRequest<TokenSession, ApiUser> request, RestBtwnResponse response) throws HaltException {

        ApiUser apiUser = toUser(request.getSession());
        if (apiUser != null) {
            request.setUser(Optional.of(apiUser));
        } else {
            throw new HaltException("Failed Authentication.");
        }
    }

    protected ApiUser toUser(Optional<TokenSession> session) {
        UUID expectedToken = UUID.fromString("2cf081ed-aa7c-4141-b634-01fb56bc96bb");
        if (session.isPresent() && expectedToken.equals(session.get().getAccessToken())) {
            return new ApiUser(UUID.randomUUID(), "Obi-Wan", "Kenobi");
        }
        return null;
    }
}

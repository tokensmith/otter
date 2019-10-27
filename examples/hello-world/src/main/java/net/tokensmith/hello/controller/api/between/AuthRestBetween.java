package net.tokensmith.hello.controller.api.between;

import net.tokensmith.hello.controller.api.model.ApiUser;
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
 * In a real application it would use the authentication header to fetch the user from a identity server or database or
 * wherever you want. If authentication fails then throw a HaltException. That will stop the request from reaching its
 * intended Resource.
 */
public class AuthRestBetween implements RestBetween<ApiUser> {
    @Override
    public void process(Method method, RestBtwnRequest<ApiUser> request, RestBtwnResponse response) throws HaltException {
        ApiUser apiUser = new ApiUser(UUID.randomUUID(), "Obi-Wan", "Kenobi");
        request.setUser(Optional.of(apiUser));
    }
}

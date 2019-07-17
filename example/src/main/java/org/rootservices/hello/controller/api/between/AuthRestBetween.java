package org.rootservices.hello.controller.api.between;

import org.rootservices.hello.controller.api.model.ApiUser;
import org.rootservices.otter.dispatch.entity.RestBtwnRequest;
import org.rootservices.otter.dispatch.entity.RestBtwnResponse;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.router.exception.HaltException;

import java.util.Optional;
import java.util.UUID;

/**
 * A Simple example of setting the user in a Authentication Between.
 */
public class AuthRestBetween implements RestBetween<ApiUser> {
    @Override
    public void process(Method method, RestBtwnRequest<ApiUser> request, RestBtwnResponse response) throws HaltException {
        ApiUser apiUser = new ApiUser(UUID.randomUUID(), "Obi-Wan", "Kenobi");
        request.setUser(Optional.of(apiUser));
    }
}

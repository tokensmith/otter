package org.rootservices.hello.controller.api.between;

import org.rootservices.hello.controller.api.model.ApiSession;
import org.rootservices.hello.controller.api.model.ApiUser;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.router.entity.between.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;

import java.util.Optional;
import java.util.UUID;

/**
 * A Simple example of setting the user in a Authentication Between.
 */
public class AuthLegacyRestBetween implements Between<ApiSession, ApiUser> {

    @Override
    public void process(Method method, Request<ApiSession, ApiUser> request, Response<ApiSession> response) throws HaltException {
        ApiUser apiUser = new ApiUser(UUID.randomUUID(), "Obi-Wan", "Kenobi");
        request.setUser(Optional.of(apiUser));
    }
}

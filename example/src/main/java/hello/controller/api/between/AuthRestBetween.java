package hello.controller.api.between;

import hello.controller.api.model.ApiSession;
import hello.controller.api.model.ApiUser;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;

import java.util.Optional;
import java.util.UUID;

/**
 * A Simple example of setting the user in a Authentication Between.
 */
public class AuthRestBetween implements Between<ApiSession, ApiUser> {

    @Override
    public void process(Method method, Request<ApiSession, ApiUser> request, Response<ApiSession> response) throws HaltException {
        ApiUser apiUser = new ApiUser(UUID.randomUUID(), "Obi-Wan", "Kenobi");
        request.setUser(Optional.of(apiUser));
    }
}

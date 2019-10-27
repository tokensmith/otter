package net.tokensmith.hello.controller.html;

import net.tokensmith.hello.security.TokenSession;
import net.tokensmith.hello.security.User;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;

public class RunTimeExceptionResource extends Resource<TokenSession, User> {
    public static String URL = "/exception(.*)";

    @Override
    public Response<TokenSession> get(Request<TokenSession, User> request, Response<TokenSession> response) {
        throw new RuntimeException("unexpected exception.");
    }

    @Override
    public Response<TokenSession> post(Request<TokenSession, User> request, Response<TokenSession> response) {
        throw new RuntimeException("unexpected exception.");
    }

    @Override
    public Response<TokenSession> put(Request<TokenSession, User> request, Response<TokenSession> response) {
        throw new RuntimeException("unexpected exception.");
    }

    @Override
    public Response<TokenSession> delete(Request<TokenSession, User> request, Response<TokenSession> response) {
        throw new RuntimeException("unexpected exception.");
    }

    @Override
    public Response<TokenSession> connect(Request<TokenSession, User> request, Response<TokenSession> response) {
        throw new RuntimeException("unexpected exception.");
    }

    @Override
    public Response<TokenSession> options(Request<TokenSession, User> request, Response<TokenSession> response) {
        throw new RuntimeException("unexpected exception.");
    }

    @Override
    public Response<TokenSession> trace(Request<TokenSession, User> request, Response<TokenSession> response) {
        throw new RuntimeException("unexpected exception.");
    }

    @Override
    public Response<TokenSession> patch(Request<TokenSession, User> request, Response<TokenSession> response) {
        throw new RuntimeException("unexpected exception.");
    }

    @Override
    public Response<TokenSession> head(Request<TokenSession, User> request, Response<TokenSession> response) {
        throw new RuntimeException("unexpected exception.");
    }
}

package integration.app.hello.controller;

import org.rootservices.otter.security.session.Session;

import java.util.UUID;

public class TokenSession implements Session {
    private UUID accessToken;

    public UUID getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(UUID accessToken) {
        this.accessToken = accessToken;
    }
}

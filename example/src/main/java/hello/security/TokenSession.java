package hello.security;


import org.rootservices.otter.controller.entity.DefaultSession;

import java.util.Objects;
import java.util.UUID;

public class TokenSession extends DefaultSession {
    private UUID accessToken;

    public TokenSession() {
    }

    public TokenSession(TokenSession from) {
        this.accessToken = from.accessToken;
    }

    public TokenSession(UUID accessToken) {
        this.accessToken = accessToken;
    }

    public UUID getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(UUID accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenSession that = (TokenSession) o;
        return Objects.equals(accessToken, that.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken);
    }
}

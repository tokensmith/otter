package helper.entity;


import org.rootservices.otter.security.session.Session;

import java.util.Objects;

public class DummySession implements Session {
    private String accessToken;
    private String refreshToken;

    public DummySession() {
    }

    public DummySession(DummySession from) {
        this.accessToken = from.accessToken;
        this.refreshToken = from.refreshToken;
    }

    public DummySession(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummySession that = (DummySession) o;
        return Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, refreshToken);
    }
}

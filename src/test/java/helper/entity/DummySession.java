package helper.entity;

import org.rootservices.otter.security.session.Session;

public class DummySession implements Session {
    private String accessToken;
    private String refreshToken;

    public DummySession() {
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
}

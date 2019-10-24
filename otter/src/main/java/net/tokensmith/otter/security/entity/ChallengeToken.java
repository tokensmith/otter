package net.tokensmith.otter.security.entity;

public class ChallengeToken {
    private String token;
    private String noise;

    public ChallengeToken(String token, String noise) {
        this.token = token;
        this.noise = noise;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNoise() {
        return noise;
    }

    public void setNoise(String noise) {
        this.noise = noise;
    }
}

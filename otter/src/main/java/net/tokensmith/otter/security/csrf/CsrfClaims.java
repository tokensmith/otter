package net.tokensmith.otter.security.csrf;


import org.rootservices.jwt.entity.jwt.Claims;

public class CsrfClaims extends Claims {
    private String challengeToken;
    private String noise;

    public String getChallengeToken() {
        return challengeToken;
    }

    public void setChallengeToken(String challengeToken) {
        this.challengeToken = challengeToken;
    }

    public String getNoise() {
        return noise;
    }

    public void setNoise(String noise) {
        this.noise = noise;
    }
}

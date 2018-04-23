package org.rootservices.otter.security.csrf;


import org.rootservices.jwt.entity.jwt.Claims;

public class CsrfClaims extends Claims {
    private String challengeToken;

    public String getChallengeToken() {
        return challengeToken;
    }

    public void setChallengeToken(String challengeToken) {
        this.challengeToken = challengeToken;
    }
}

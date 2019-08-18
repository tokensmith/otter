package org.rootservices.hello.controller.html.presenter;


public class LoginPresenter {
    private String email;
    private String csrfChallengeToken;

    public LoginPresenter(String email, String csrfChallengeToken) {
        this.email = email;
        this.csrfChallengeToken = csrfChallengeToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCsrfChallengeToken() {
        return csrfChallengeToken;
    }

    public void setCsrfChallengeToken(String csrfChallengeToken) {
        this.csrfChallengeToken = csrfChallengeToken;
    }
}

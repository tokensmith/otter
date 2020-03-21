package net.tokensmith.otter.security.csrf;

import net.tokensmith.otter.security.RandomString;
import net.tokensmith.otter.security.csrf.Csrf;
import net.tokensmith.otter.security.csrf.exception.CsrfException;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by tommackenzie on 4/9/16.
 */
public class SynchronizerToken implements Csrf {

    private static String POST = "POST";
    private static String PUT = "PUT";
    private static String DELETE = "DELETE";
    protected static String CHALLENGE_TOKEN_SESSION_NAME = "csrfToken";
    protected static String CHALLENGE_TOKEN_FORM_NAME = "csrfToken";

    private RandomString randomString;

    public SynchronizerToken(RandomString randomString) {
        this.randomString = randomString;
    }

    public void checkTokens(HttpServletRequest httpRequest) throws CsrfException {
        Optional<String> sessionChallengeToken = getChallengeTokenFromSession(httpRequest);

        if (requestMethodRequiresChallengeToken(httpRequest.getMethod())) {
            Optional<String> formChallengeToken = getChallengeTokenFromForm(httpRequest);

            if (!doTokensMatch(sessionChallengeToken, formChallengeToken)) {
                throw new CsrfException("challenge tokens do not match");
            }

        } else if (!sessionChallengeToken.isPresent()) {
            insertChallengeTokenIntoSession(httpRequest);
        }
    }
    
    protected boolean doTokensMatch(Optional<String> sessionChallengeToken, Optional<String> formChallengeToken) {

        if (!sessionChallengeToken.isPresent() || !formChallengeToken.isPresent() ) {
            return false;
        }

        if (!sessionChallengeToken.get().equals(formChallengeToken.get())) {
            return false;
        }

        return true;
    }

    protected boolean requestMethodRequiresChallengeToken(String method) {
        return (POST.equalsIgnoreCase(method) || PUT.equalsIgnoreCase(method) || DELETE.equalsIgnoreCase(method));
    }

    protected Optional<String> getChallengeTokenFromSession(HttpServletRequest request) {
        return Optional.ofNullable((String) request.getSession().getAttribute(CHALLENGE_TOKEN_SESSION_NAME));
    }
    
    protected void insertChallengeTokenIntoSession(HttpServletRequest request) throws CsrfException {
        String challengeToken = randomString.run();
        byte[] bytes;
        try {
            bytes = challengeToken.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CsrfException("could not encode challenge token");
        }

        String encodedChallengeToken =  Base64.getEncoder().encodeToString(bytes);
        request.getSession().setAttribute(CHALLENGE_TOKEN_SESSION_NAME, encodedChallengeToken);
    }

    protected Optional<String> getChallengeTokenFromForm(HttpServletRequest request) {
        return Optional.ofNullable(request.getParameter(CHALLENGE_TOKEN_FORM_NAME));
    }
}

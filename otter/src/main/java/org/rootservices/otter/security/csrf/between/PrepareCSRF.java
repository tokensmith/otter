package org.rootservices.otter.security.csrf.between;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.request.Request;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.router.entity.between.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.csrf.CsrfClaims;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.security.csrf.exception.CsrfException;
import org.rootservices.otter.security.entity.ChallengeToken;

import java.io.ByteArrayOutputStream;
import java.util.Optional;


/**
 * Executed before a request reaches a resource to set the CSRF cookie and
 * assign the same token to the request.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public class PrepareCSRF<S, U> implements Between<S, U> {
    protected static Logger LOGGER = LoggerFactory.getLogger(PrepareCSRF.class);
    private CookieConfig cookieConfig;
    private DoubleSubmitCSRF doubleSubmitCSRF;

    public PrepareCSRF(DoubleSubmitCSRF doubleSubmitCSRF) {
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }

    public PrepareCSRF(CookieConfig cookieConfig, DoubleSubmitCSRF doubleSubmitCSRF) {
        this.cookieConfig = cookieConfig;
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }

    @Override
    public void process(Method method, Request<S, U> request, Response<S> response) throws HaltException {
        if (response.getCookies().get(cookieConfig.getName()) == null) {
            String challengeToken = doubleSubmitCSRF.makeChallengeToken();

            String cookieNoise = doubleSubmitCSRF.makeChallengeToken();
            String formNoise = doubleSubmitCSRF.makeChallengeToken();

            ChallengeToken cookieChallengeToken = new ChallengeToken(challengeToken, cookieNoise);
            ChallengeToken formChallengeToken = new ChallengeToken(challengeToken, formNoise);

            try {
                Cookie csrfCookie = doubleSubmitCSRF.makeCsrfCookie(
                        cookieConfig.getName(), cookieChallengeToken, cookieConfig.getSecure(), cookieConfig.getAge(),
                        cookieConfig.getHttpOnly()
                );
                response.getCookies().put(cookieConfig.getName(), csrfCookie);

                ByteArrayOutputStream formValue = doubleSubmitCSRF.toJwt(formChallengeToken);
                request.setCsrfChallenge(Optional.of(formValue.toString()));

            } catch (CsrfException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            JsonWebToken csrfJwt = null;
            try {
                csrfJwt = doubleSubmitCSRF.csrfToJwt(response.getCookies().get(cookieConfig.getName()).getValue());
            } catch (CsrfException e) {
                LOGGER.error(e.getMessage(), e);
            }
            CsrfClaims claims = (CsrfClaims) csrfJwt.getClaims();
            request.setCsrfChallenge(Optional.of(claims.getChallengeToken()));
        }
    }

    public CookieConfig getCookieConfig() {
        return cookieConfig;
    }

    public void setCookieConfig(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }

    public DoubleSubmitCSRF getDoubleSubmitCSRF() {
        return doubleSubmitCSRF;
    }

    public void setDoubleSubmitCSRF(DoubleSubmitCSRF doubleSubmitCSRF) {
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }
}

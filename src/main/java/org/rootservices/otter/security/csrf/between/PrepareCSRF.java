package org.rootservices.otter.security.csrf.between;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.csrf.CsrfClaims;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.security.csrf.exception.CsrfException;

import java.util.Optional;


/**
 * Executed before a request reaches a resource to set the CSRF cookie and
 * assign it to the request.
 */
public class PrepareCSRF implements Between {
    protected static Logger logger = LogManager.getLogger(PrepareCSRF.class);
    private String cookieName;
    private Boolean isSecure;
    private Integer maxAge;
    private DoubleSubmitCSRF doubleSubmitCSRF;

    public PrepareCSRF(DoubleSubmitCSRF doubleSubmitCSRF) {
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }

    public PrepareCSRF(String cookieName, Boolean isSecure, Integer maxAge, DoubleSubmitCSRF doubleSubmitCSRF) {
        this.cookieName = cookieName;
        this.isSecure = isSecure;
        this.maxAge = maxAge;
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }

    @Override
    public void process(Method method, Request request, Response response) throws HaltException {
        if (response.getCookies().get(cookieName) == null) {
            String challengeToken = doubleSubmitCSRF.makeChallengeToken();
            try {
                Cookie csrfCookie = doubleSubmitCSRF.makeCsrfCookie(
                    cookieName, challengeToken, isSecure, maxAge
                );
                response.getCookies().put(cookieName, csrfCookie);
                request.setCsrfChallenge(Optional.of(challengeToken));
            } catch (CsrfException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            JsonWebToken csrfJwt = null;
            try {
                csrfJwt = doubleSubmitCSRF.csrfCookieValueToJwt(response.getCookies().get(cookieName).getValue());
            } catch (CsrfException e) {
                logger.error(e.getMessage(), e);
            }
            CsrfClaims claims = (CsrfClaims) csrfJwt.getClaims();
            request.setCsrfChallenge(Optional.of(claims.getChallengeToken()));
        }
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public Boolean getSecure() {
        return isSecure;
    }

    public void setSecure(Boolean secure) {
        isSecure = secure;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public DoubleSubmitCSRF getDoubleSubmitCSRF() {
        return doubleSubmitCSRF;
    }

    public void setDoubleSubmitCSRF(DoubleSubmitCSRF doubleSubmitCSRF) {
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }
}

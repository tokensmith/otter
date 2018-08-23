package org.rootservices.otter.security.csrf.between;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.csrf.CsrfClaims;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.security.csrf.exception.CsrfException;
import org.rootservices.otter.security.session.Session;

import java.util.Optional;


/**
 * Executed before a request reaches a resource to set the CSRF cookie and
 * assign it to the request.
 */
public class PrepareCSRF<T extends Session> implements Between<T> {
    protected static Logger logger = LogManager.getLogger(PrepareCSRF.class);
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
    public void process(Method method, Request<T> request, Response<T> response) throws HaltException {
        if (response.getCookies().get(cookieConfig.getName()) == null) {
            String challengeToken = doubleSubmitCSRF.makeChallengeToken();
            try {
                Cookie csrfCookie = doubleSubmitCSRF.makeCsrfCookie(
                        cookieConfig.getName(), challengeToken, cookieConfig.getSecure(), cookieConfig.getAge()
                );
                response.getCookies().put(cookieConfig.getName(), csrfCookie);
                request.setCsrfChallenge(Optional.of(challengeToken));
            } catch (CsrfException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            JsonWebToken csrfJwt = null;
            try {
                csrfJwt = doubleSubmitCSRF.csrfCookieValueToJwt(response.getCookies().get(cookieConfig.getName()).getValue());
            } catch (CsrfException e) {
                logger.error(e.getMessage(), e);
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

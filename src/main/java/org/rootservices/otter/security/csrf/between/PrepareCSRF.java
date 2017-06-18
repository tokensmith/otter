package org.rootservices.otter.security.csrf.between;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.security.csrf.exception.CsrfException;


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
    public Boolean process(Method method, Request request, Response response) {
        if (response.getCookies().get(cookieName) == null) {
            String challengeToken = doubleSubmitCSRF.makeChallengeToken();
            try {
                Cookie csrfCookie = doubleSubmitCSRF.makeCsrfCookie(
                    cookieName, challengeToken, isSecure, maxAge
                );
                response.getCookies().put(cookieName, csrfCookie);
            } catch (CsrfException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return true;
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

package org.rootservices.otter.security.csrf;

import org.rootservices.otter.security.csrf.exception.CsrfException;

import javax.servlet.http.HttpServletRequest;


public interface Csrf {
    void checkTokens(HttpServletRequest httpRequest) throws CsrfException;
}

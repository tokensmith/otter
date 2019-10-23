package net.tokensmith.otter.security.csrf;

import net.tokensmith.otter.security.csrf.exception.CsrfException;

import javax.servlet.http.HttpServletRequest;


public interface Csrf {
    void checkTokens(HttpServletRequest httpRequest) throws CsrfException;
}

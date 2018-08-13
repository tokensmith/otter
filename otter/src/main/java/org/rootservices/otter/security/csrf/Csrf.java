package org.rootservices.otter.security.csrf;

import org.rootservices.otter.security.csrf.exception.CsrfException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by tommackenzie on 4/9/16.
 */
public interface Csrf {
    void checkTokens(HttpServletRequest httpRequest) throws CsrfException;
}

package org.rootservices.otter.security.csrf.exception;

/**
 * Created by tommackenzie on 4/9/16.
 */
public class CsrfException extends Exception {
    public CsrfException(String message) {
        super(message);
    }

    public CsrfException(String message, Throwable cause) {
        super(message, cause);
    }
}

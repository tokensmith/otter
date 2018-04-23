package org.rootservices.otter.router.exception;


public class CsrfException extends HaltException {
    public CsrfException(String message) {
        super(message);
    }
}

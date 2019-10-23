package net.tokensmith.otter.router.exception;


public class CsrfException extends HaltException {
    public CsrfException(String message) {
        super(message);
    }
}

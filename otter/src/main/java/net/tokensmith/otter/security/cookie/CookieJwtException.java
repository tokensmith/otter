package net.tokensmith.otter.security.cookie;

public class CookieJwtException extends Exception {
    public CookieJwtException(String message) {
        super(message);
    }

    public CookieJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}

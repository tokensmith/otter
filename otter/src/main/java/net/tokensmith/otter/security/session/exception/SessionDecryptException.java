package net.tokensmith.otter.security.session.exception;

/**
 * Used when something went wrong attempting to decrypt a session.
 */
public class SessionDecryptException extends Exception {
    public SessionDecryptException(String message, Throwable cause) {
        super(message, cause);
    }
}

package net.tokensmith.otter.security.session.between.exception;

/**
 * Used when something went wrong attempting to decrypt a session.
 */
public class SessionDecryptException extends Exception {
    public SessionDecryptException(String message, Throwable cause) {
        super(message, cause);
    }
}

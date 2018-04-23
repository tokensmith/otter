package org.rootservices.otter.security.session.between.exception;

/**
 * Used when for any reason a session could not be encrypted.
 * The cause will have the stack trace of what caused the failure.
 */
public class EncryptSessionException extends Exception {
    public EncryptSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}

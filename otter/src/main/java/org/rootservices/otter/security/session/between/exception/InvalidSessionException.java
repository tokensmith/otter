package org.rootservices.otter.security.session.between.exception;

/**
 * Used when the actual payload of the session was not in a compact JWT format.
 * More specifically the payload could not be de-serialized to it's expect payload type.
 */
public class InvalidSessionException extends Exception {
    public InvalidSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}

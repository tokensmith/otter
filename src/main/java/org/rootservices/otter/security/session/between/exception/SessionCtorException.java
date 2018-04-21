package org.rootservices.otter.security.session.between.exception;

/**
 * Used when a implementation of Session did not have a copy constructor.
 */
public class SessionCtorException extends Exception {
    public SessionCtorException(String message, Throwable cause) {
        super(message, cause);
    }
}

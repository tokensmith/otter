package net.tokensmith.otter.dispatch.json.validator.exception;

import net.tokensmith.otter.dispatch.json.validator.ValidateError;

import java.util.List;

public class ValidateException extends Exception {
    List<ValidateError> errors;

    public ValidateException(String message, List<ValidateError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<ValidateError> getErrors() {
        return errors;
    }
}

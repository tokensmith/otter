package net.tokensmith.otter.dispatch.json.validator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RestValidate implements Validate {
    protected static Logger LOGGER = LoggerFactory.getLogger(RestValidate.class);

    private Validator validator;

    public RestValidate(Validator validator) {
        this.validator = validator;
    }

    @Override
    public <T> List<ValidateError> validate(T payload) {

        List<ValidateError> errors = new ArrayList<>();

        Set<ConstraintViolation<T>> violations  = validator.validate(payload);

        for (ConstraintViolation<T> violation : violations) {
            LOGGER.error(violation.getMessage());

            String value = null;
            if (Objects.nonNull(violation.getInvalidValue())) {
                value = violation.getInvalidValue().toString();
            }

            errors.add(
                new ValidateError(
                    violation.getPropertyPath().toString(),
                    value,
                    violation.getMessage()
                )
            );
        }

        return errors;
    }
}

package net.tokensmith.otter.dispatch.json.validator;

import java.util.List;

public interface Validate {
    <T> List<ValidateError> validate(T payload);
}

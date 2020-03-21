package helper.fake;

import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.dispatch.json.validator.ValidateError;

import java.util.List;

public class FakeValidate implements Validate {
    @Override
    public <T> List<ValidateError> validate(T payload) {
        return null;
    }
}

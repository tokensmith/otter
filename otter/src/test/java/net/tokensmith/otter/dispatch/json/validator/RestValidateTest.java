package net.tokensmith.otter.dispatch.json.validator;

import helper.fake.Payload;
import org.junit.Before;
import org.junit.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestValidateTest {

    private Validate subject;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        subject = new RestValidate(validator);
    }

    @Test
    public void validateShouldPass() {
        Payload payload = new Payload();
        payload.setId(UUID.randomUUID());
        payload.setAge(10);

        List<ValidateError> actual = subject.validate(payload);

        assertThat(actual.size(), is(0));
    }

    @Test
    public void validateShouldFail() {
        Payload payload = new Payload();
        payload.setAge(10);

        List<ValidateError> actual = subject.validate(payload);

        assertThat(actual.size(), is(1));

        assertThat(actual.get(0).getKey(), is("id"));
        assertThat(actual.get(0).getValue(), is(nullValue()));
        assertThat(actual.get(0).getDescription(), is("must not be null"));
    }

}
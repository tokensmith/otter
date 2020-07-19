package net.tokensmith.otter.translator;

import helper.entity.model.DummyPayload;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import net.tokensmith.otter.translator.exception.DeserializationException;
import net.tokensmith.otter.translator.exception.DuplicateKeyException;
import net.tokensmith.otter.translator.exception.InvalidPayloadException;
import net.tokensmith.otter.translator.exception.InvalidValueException;
import net.tokensmith.otter.translator.exception.Reason;
import net.tokensmith.otter.translator.exception.UnknownKeyException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class JsonTranslatorTest {
    private JsonTranslator<DummyPayload> subject;

    @Before
    public void setUp() {
        TranslatorAppFactory factory = new TranslatorAppFactory();
        subject = factory.jsonTranslator(DummyPayload.class);
    }

    @Test
    public void fromWithSpecificCauseShouldBeOk() throws Exception {
        String json="{\"integer\": 5, \"string\": \"foo\", \"local_date\": \"2019-01-01\"}";

        DummyPayload actual = (DummyPayload) subject.fromWithSpecificCause(json.getBytes());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getInteger(), is(5));
        assertThat(actual.getString(), is("foo"));
        assertThat(actual.getLocalDate(), is(LocalDate.of(2019,01,01)));
    }

    @Test
    public void fromWithSpecificCauseShouldThrowDuplicateKeyException() throws Exception {
        String json = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";

        DuplicateKeyException actual = null;
        try {
            subject.fromWithSpecificCause(json.getBytes());
        } catch(DuplicateKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey(), is("integer"));
    }

    @Test
    public void fromWithSpecificCauseShouldThrowUnknownKeyException() throws Exception {
        String json = "{\"integer\": 5, \"unknown_key\": \"4\", \"local_date\": \"2019-01-01\"}";

        UnknownKeyException actual = null;
        try {
            subject.fromWithSpecificCause(json.getBytes());
        } catch(UnknownKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey(), is("unknown_key"));
    }

    @Test
    public void fromWithSpecificCauseShouldThrowInvalidValueException() throws Exception {
        String json = "{\"integer\": \"not a integer\", \"string\": \"foo\", \"local_date\": \"2019-01-01\"}";

        InvalidValueException actual = null;
        try {
            subject.fromWithSpecificCause(json.getBytes());
        } catch(InvalidValueException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey(), is("integer"));
    }

    @Test
    public void fromWithSpecificCauseShouldThrowInvalidPayloadException() throws Exception {
        String json = "{";

        InvalidPayloadException actual = null;
        try {
            subject.fromWithSpecificCause(json.getBytes());
        } catch(InvalidPayloadException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void fromWhenDuplicateShouldThrowDeserializationException() throws Exception {
        String json = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";

        DeserializationException actual = null;
        try {
            subject.from(json.getBytes());
        } catch(DeserializationException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(instanceOf(DuplicateKeyException.class)));
        assertThat(actual.getReason(), is(Reason.DUPLICATE_KEY));
        assertThat(actual.getKey(), is(notNullValue()));
        assertThat(actual.getKey().isPresent(), is(true));
        assertThat(actual.getKey().get(), is("integer"));

        DuplicateKeyException actualCause = (DuplicateKeyException) actual.getCause();
        assertThat(actualCause.getKey(), is("integer"));
    }

    @Test
    public void fromWhenUnknownKeyShouldThrowDeserializationException() throws Exception {
        String json = "{\"integer\": 5, \"unknown_key\": \"4\", \"local_date\": \"2019-01-01\"}";

        DeserializationException actual = null;
        try {
            subject.from(json.getBytes());
        } catch(DeserializationException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(instanceOf(UnknownKeyException.class)));
        UnknownKeyException actualCause = (UnknownKeyException) actual.getCause();
        assertThat(actualCause.getKey(), is("unknown_key"));
    }

    @Test
    public void fromWhenInvalidValueShouldThrowDeserializationException() throws Exception {
        String json = "{\"integer\": \"not a integer\", \"string\": \"foo\", \"local_date\": \"2019-01-01\"}";

        DeserializationException actual = null;
        try {
            subject.from(json.getBytes());
        } catch(DeserializationException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(instanceOf(InvalidValueException.class)));
        InvalidValueException actualCause = (InvalidValueException) actual.getCause();
        assertThat(actualCause.getKey(), is("integer"));
    }

    @Test
    public void fromWhenInvalidPayloadShouldThrowDeserializationException() throws Exception {
        String json = "{";

        DeserializationException actual = null;
        try {
            subject.from(json.getBytes());
        } catch(DeserializationException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(instanceOf(InvalidPayloadException.class)));
    }


    @Test
    public void toShouldBeOk() throws Exception {
        DummyPayload dummy = new DummyPayload();
        dummy.setInteger(5);
        dummy.setString("string");
        dummy.setLocalDate(LocalDate.of(2017, 05, 20));
        dummy.setIntegerOptional(Optional.empty());

        byte[] out = subject.to(dummy);

        assertThat(out, is(notNullValue()));
        assertThat(new String(out), is("{\"integer\":5,\"string\":\"string\",\"local_date\":\"2017-05-20\",\"integer_optional\":null}"));
    }
}
package org.rootservices.otter.translator;

import helper.entity.DummyPayload;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.translator.exception.DuplicateKeyException;
import org.rootservices.otter.translator.exception.InvalidPayloadException;
import org.rootservices.otter.translator.exception.InvalidValueException;
import org.rootservices.otter.translator.exception.UnknownKeyException;


import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class JsonTranslatorTest {
    private JsonTranslator<DummyPayload> subject;

    @Before
    public void setUp() {
        OtterAppFactory factory = new OtterAppFactory();
        subject = new JsonTranslator<DummyPayload>(factory.objectReader(), factory.objectWriter());
    }

    @Test
    public void fromShouldBeOk() throws Exception {
        String json="{\"integer\": 5, \"string\": \"foo\", \"local_date\": \"2019-01-01\"}";

        DummyPayload actual = (DummyPayload) subject.from(json.getBytes(), DummyPayload.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getInteger(), is(5));
        assertThat(actual.getString(), is("foo"));
        assertThat(actual.getLocalDate(), is(LocalDate.of(2019,01,01)));
    }

    @Test
    public void fromShouldThrowDuplicateKeyException() throws Exception {
        String json = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";

        DuplicateKeyException actual = null;
        try {
            subject.from(json.getBytes(), DummyPayload.class);
        } catch(DuplicateKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey(), is("integer"));
    }

    @Test
    public void fromShouldThrowUnknownKeyException() throws Exception {
        String json = "{\"integer\": 5, \"unknown_key\": \"4\", \"local_date\": \"2019-01-01\"}";

        UnknownKeyException actual = null;
        try {
            subject.from(json.getBytes(), DummyPayload.class);
        } catch(UnknownKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey(), is("unknown_key"));
    }

    @Test
    public void fromShouldThrowInvalidValueException() throws Exception {
        String json = "{\"integer\": \"not a integer\", \"string\": \"foo\", \"local_date\": \"2019-01-01\"}";

        InvalidValueException actual = null;
        try {
            subject.from(json.getBytes(), DummyPayload.class);
        } catch(InvalidValueException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey(), is("integer"));
    }

    @Test
    public void fromShouldThrowInvalidPayloadException() throws Exception {
        String json = "{";

        InvalidPayloadException actual = null;
        try {
            subject.from(json.getBytes(), DummyPayload.class);
        } catch(InvalidPayloadException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }


    @Test
    public void toShouldBeOk() throws Exception {
        DummyPayload dummy = new DummyPayload();
        dummy.setInteger(5);
        dummy.setString("string");
        dummy.setLocalDate(LocalDate.of(2017, 05, 20));
        dummy.setIntegerOptional(Optional.empty());

        OutputStream out = subject.to(dummy);

        assertThat(out, is(notNullValue()));
        assertThat(out.toString(), is("{\"integer\":5,\"string\":\"string\",\"local_date\":\"2017-05-20\",\"integer_optional\":null}"));
    }
}
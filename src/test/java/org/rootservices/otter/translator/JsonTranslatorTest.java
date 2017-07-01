package org.rootservices.otter.translator;

import helper.entity.Dummy;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.config.AppFactory;
import org.rootservices.otter.translator.exception.DuplicateKeyException;
import org.rootservices.otter.translator.exception.InvalidPayloadException;
import org.rootservices.otter.translator.exception.InvalidValueException;
import org.rootservices.otter.translator.exception.UnknownKeyException;
import suite.UnitTest;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


@Category(UnitTest.class)
public class JsonTranslatorTest {
    private JsonTranslator<Dummy> subject;

    @Before
    public void setUp() {
        AppFactory factory = new AppFactory();
        subject = new JsonTranslator<Dummy>(factory.objectMapper());
    }

    @Test
    public void fromShouldBeOk() throws Exception {
        StringReader sr = new StringReader("{\"integer\": 5, \"string\": \"foo\", \"local_date\": \"2019-01-01\"}");
        BufferedReader json = new BufferedReader(sr);

        Dummy actual = (Dummy) subject.from(json, Dummy.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getInteger(), is(5));
        assertThat(actual.getString(), is("foo"));
        assertThat(actual.getLocalDate(), is(LocalDate.of(2019,01,01)));
    }

    @Test
    public void fromShouldThrowDuplicateKeyException() throws Exception {
        StringReader sr = new StringReader("{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}");
        BufferedReader json = new BufferedReader(sr);

        DuplicateKeyException actual = null;
        try {
            subject.from(json, Dummy.class);
        } catch(DuplicateKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey(), is("integer"));
    }

    @Test
    public void fromShouldThrowUnknownKeyException() throws Exception {
        StringReader sr = new StringReader("{\"integer\": 5, \"unknown_key\": \"4\", \"local_date\": \"2019-01-01\"}");
        BufferedReader json = new BufferedReader(sr);

        UnknownKeyException actual = null;
        try {
            subject.from(json, Dummy.class);
        } catch(UnknownKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey(), is("unknown_key"));
    }

    @Test
    public void fromShouldThrowInvalidValueException() throws Exception {
        StringReader sr = new StringReader("{\"integer\": \"not a integer\", \"string\": \"foo\", \"local_date\": \"2019-01-01\"}");
        BufferedReader json = new BufferedReader(sr);

        InvalidValueException actual = null;
        try {
            subject.from(json, Dummy.class);
        } catch(InvalidValueException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKey(), is("integer"));
    }

    @Test
    public void fromShouldThrowInvalidPayloadException() throws Exception {
        StringReader sr = new StringReader("{");
        BufferedReader json = new BufferedReader(sr);

        InvalidPayloadException actual = null;
        try {
            subject.from(json, Dummy.class);
        } catch(InvalidPayloadException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }


    @Test
    public void toShouldBeOk() throws Exception {
        Dummy dummy = new Dummy();
        dummy.setInteger(5);
        dummy.setString("string");
        dummy.setLocalDate(LocalDate.of(2017, 05, 20));
        dummy.setIntegerOptional(Optional.empty());

        OutputStream out = subject.to(dummy);

        assertThat(out, is(notNullValue()));
        assertThat(out.toString(), is("{\"integer\":5,\"string\":\"string\",\"local_date\":\"2017-05-20\",\"integer_optional\":null}"));
    }
}
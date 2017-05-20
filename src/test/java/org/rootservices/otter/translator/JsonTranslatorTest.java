package org.rootservices.otter.translator;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.config.AppFactory;
import org.rootservices.otter.translator.exception.DuplicateKeyException;
import org.rootservices.otter.translator.exception.InvalidPayloadException;
import org.rootservices.otter.translator.exception.InvalidValueException;
import org.rootservices.otter.translator.exception.UnknownKeyException;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class JsonTranslatorTest {
    private JsonTranslator<Dummy> subject;

    @Before
    public void setUp() {
        AppFactory factory = new AppFactory();
        subject = new JsonTranslator<>(factory.objectMapper());
    }

    @Test
    public void shouldBeOk() throws Exception {
        StringReader sr = new StringReader("{\"integer\": 5, \"string\": \"foo\", \"local_date\": \"2019-01-01\"}");
        BufferedReader json = new BufferedReader(sr);

        Dummy actual = subject.from(json, Dummy.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getInteger(), is(5));
        assertThat(actual.getString(), is("foo"));
        assertThat(actual.getLocalDate(), is(LocalDate.of(2019,01,01)));
    }

    @Test
    public void shouldThrowDuplicateKeyException() throws Exception {
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
    public void shouldThrowUnknownKeyException() throws Exception {
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
    public void shouldThrowInvalidValueException() throws Exception {
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
    public void shouldThrowInvalidPayloadException() throws Exception {
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
}
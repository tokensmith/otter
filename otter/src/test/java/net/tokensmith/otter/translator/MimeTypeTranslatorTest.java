package net.tokensmith.otter.translator;

import net.tokensmith.otter.controller.entity.mime.MimeType;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MimeTypeTranslatorTest {
    private MimeTypeTranslator subject;

    @Before
    public void setUp() throws Exception {
        subject = new MimeTypeTranslator();
    }

    @Test
    public void toWhenNullShould() {
        String from = null;

        MimeType actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getType(), is(nullValue()));
        assertThat(actual.getSubType(), is(nullValue()));
        assertThat(actual.getParameters(), is(notNullValue()));
        assertThat(actual.getParameters().size(), is(0));
    }


    @Test
    public void toWhenTextHtmlShouldAddUsAsciiAndBeOK() {
        String from = "text/html";

        MimeType actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getType(), is("text"));
        assertThat(actual.getSubType(), is("html"));
        assertThat(actual.getParameters(), is(notNullValue()));
        assertThat(actual.getParameters().size(), is(1));
        assertThat(actual.getParameters().get("charset"), is("us-ascii"));
    }

    @Test
    public void toWhenTextHtmlAndUtf8ShouldBeOK() {
        String from = "text/html; charset=utf-8";

        MimeType actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getType(), is("text"));
        assertThat(actual.getSubType(), is("html"));
        assertThat(actual.getParameters(), is(notNullValue()));
        assertThat(actual.getParameters().size(), is(1));
        assertThat(actual.getParameters().get("charset"), is("utf-8"));
    }

    @Test
    public void toWhenJsonAndUtf8ShouldBeOK() {
        String from = "application/json; charset=utf-8;";

        MimeType actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getType(), is("application"));
        assertThat(actual.getSubType(), is("json"));
        assertThat(actual.getParameters(), is(notNullValue()));
        assertThat(actual.getParameters().size(), is(1));
        assertThat(actual.getParameters().get("charset"), is("utf-8"));
    }


    @Test
    public void toWhenJsonAndUtf8AndCharsetUpperCaseShouldLowerCaseCharset() {
        String from = "application/json; charset=UTF-8;";

        MimeType actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getType(), is("application"));
        assertThat(actual.getSubType(), is("json"));
        assertThat(actual.getParameters(), is(notNullValue()));
        assertThat(actual.getParameters().size(), is(1));
        assertThat(actual.getParameters().get("charset"), is("utf-8"));
    }

    @Test
    public void toWhenFormAndUtf8ShouldBeOK() {
        String from = "application/x-www-form-urlencoded; charset=utf-8;";

        MimeType actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getType(), is("application"));
        assertThat(actual.getSubType(), is("x-www-form-urlencoded"));
        assertThat(actual.getParameters(), is(notNullValue()));
        assertThat(actual.getParameters().size(), is(1));
        assertThat(actual.getParameters().get("charset"), is("utf-8"));
    }


    @Test
    public void toWhenMultiPartShouldAddUsAsciiAndBeOK() {
        String from = "multipart/mixed; boundary=gc0p4Jq0M2Yt08j34c0p";

        MimeType actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getType(), is("multipart"));
        assertThat(actual.getSubType(), is("mixed"));
        assertThat(actual.getParameters(), is(notNullValue()));
        assertThat(actual.getParameters().size(), is(2));
        assertThat(actual.getParameters().get("boundary"), is("gc0p4Jq0M2Yt08j34c0p"));
        assertThat(actual.getParameters().get("charset"), is("us-ascii"));
    }

    @Test
    public void toWhenMultiPartWithColonShouldAddUsAsciiAndBeOK() {
        String from = "multipart/mixed; boundary=\"gc0pJq0M:08jU534c0p\"";

        MimeType actual = subject.to(from);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getType(), is("multipart"));
        assertThat(actual.getSubType(), is("mixed"));
        assertThat(actual.getParameters(), is(notNullValue()));
        assertThat(actual.getParameters().size(), is(2));
        assertThat(actual.getParameters().get("boundary"), is("gc0pJq0M:08jU534c0p"));
        assertThat(actual.getParameters().get("charset"), is("us-ascii"));
    }
}
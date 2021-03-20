package net.tokensmith.otter.controller.entity.mime;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MimeTypeTest {

    @Test
    public void testToString() {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("charset", "utf-8");
        parameters.put("zzzz", "zzzz");
        parameters.put("aaaa", "aaaa");

        MimeType subject = new MimeType("text", "html", parameters);

        String actual = subject.toString();

        assertThat(actual, is("text/html; charset=utf-8; zzzz=zzzz; aaaa=aaaa;"));
    }

    @Test
    public void testEqualsShouldBeTrue() {
        Map<String, String> parametersA = new LinkedHashMap<>();
        parametersA.put("charset", "utf-8");

        MimeType textHtmlA = new MimeType("text", "html", parametersA);

        Map<String, String> parametersB = new LinkedHashMap<>();
        parametersB.put("charset", "utf-8");
        MimeType textHtmlB = new MimeType("text", "html", parametersB);

        Boolean actual = textHtmlA.equals(textHtmlB);
        assertThat(actual, is(true));
    }

    @Test
    public void testEqualsShouldBeFalse() {
        Map<String, String> parametersA = new LinkedHashMap<>();
        parametersA.put("charset", "us-ascii");

        MimeType textHtmlA = new MimeType("text", "html", parametersA);

        Map<String, String> parametersB = new LinkedHashMap<>();
        parametersB.put("charset", "utf-8");
        MimeType textHtmlB = new MimeType("text", "html", parametersB);

        Boolean actual = textHtmlA.equals(textHtmlB);
        assertThat(actual, is(false));
    }

    @Test
    public void testEqualsWhenDifferentCaseShouldBeFalse() {
        Map<String, String> parametersA = new LinkedHashMap<>();
        parametersA.put("charset", "UTF-8");

        MimeType textHtmlA = new MimeType("text", "html", parametersA);

        Map<String, String> parametersB = new LinkedHashMap<>();
        parametersB.put("charset", "utf-8");
        MimeType textHtmlB = new MimeType("text", "html", parametersB);

        Boolean actual = textHtmlA.equals(textHtmlB);
        assertThat(actual, is(false));
    }
}
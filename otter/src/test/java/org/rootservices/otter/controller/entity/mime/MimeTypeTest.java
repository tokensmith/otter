package org.rootservices.otter.controller.entity.mime;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
}
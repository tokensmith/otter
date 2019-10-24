package net.tokensmith.otter.controller.builder;

import org.junit.Test;
import net.tokensmith.otter.controller.entity.mime.MimeType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class MimeTypeBuilderTest {

    @Test
    public void html() {
        MimeType actual = new MimeTypeBuilder().html().build();

        assertThat(actual.getType(), is("text"));
        assertThat(actual.getSubType(), is("html"));
        assertThat(actual.getParameters().size(), is(1));
        assertThat(actual.getParameters().get("charset"), is("utf-8"));
        assertThat(actual.toString(), is("text/html; charset=utf-8;"));
    }

    @Test
    public void json() {
        MimeType actual = new MimeTypeBuilder().json().build();

        assertThat(actual.getType(), is("application"));
        assertThat(actual.getSubType(), is("json"));
        assertThat(actual.getParameters().size(), is(1));
        assertThat(actual.getParameters().get("charset"), is("utf-8"));
        assertThat(actual.toString(), is("application/json; charset=utf-8;"));
    }

    @Test
    public void jwt() {
        MimeType actual = new MimeTypeBuilder().jwt().build();

        assertThat(actual.getType(), is("application"));
        assertThat(actual.getSubType(), is("jwt"));
        assertThat(actual.getParameters().size(), is(1));
        assertThat(actual.getParameters().get("charset"), is("utf-8"));
        assertThat(actual.toString(), is("application/jwt; charset=utf-8;"));
    }

    @Test
    public void form() {
        MimeType actual = new MimeTypeBuilder().form().build();

        assertThat(actual.getType(), is("application"));
        assertThat(actual.getSubType(), is("x-www-form-urlencoded"));
        assertThat(actual.getParameters().size(), is(1));
        assertThat(actual.getParameters().get("charset"), is("utf-8"));
        assertThat(actual.toString(), is("application/x-www-form-urlencoded; charset=utf-8;"));
    }
}
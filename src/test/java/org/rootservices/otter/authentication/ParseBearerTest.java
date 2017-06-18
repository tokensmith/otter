package org.rootservices.otter.authentication;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.authentication.exception.BearerException;
import org.rootservices.otter.authentication.exception.HttpBasicException;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 12/13/16.
 */
public class ParseBearerTest {
    private ParseBearer subject;

    @Before
    public void setUp() {
        subject = new ParseBearer();
    }

    @Test
    public void testRun() throws Exception {
        String credentials = "Bearer foo";

        String token = subject.parse(credentials);
        assertThat(token, is("foo"));
    }

    @Test(expected=BearerException.class)
    public void testHeaderIsEmpty() throws Exception {
        subject.parse("");
    }

    @Test(expected=BearerException.class)
    public void testHeaderIsNull() throws Exception {
        subject.parse(null);
    }

    @Test(expected=BearerException.class)
    public void testHeaderIsNotBasic() throws Exception {
        subject.parse("foo");
    }

    @Test(expected=BearerException.class)
    public void testHeaderMissingTokenValue() throws Exception {
        subject.parse("Basic ");
    }
}
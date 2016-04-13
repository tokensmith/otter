package org.rootservices.otter.authentication;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.authentication.exception.HttpBasicException;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * Created by tommackenzie on 6/4/15.
 *
 */
public class ParseHttpBasicImplTest {

    private ParseHttpBasic subject;

    @Before
    public void setUp() {
        subject = new ParseHttpBasicImpl();
    }
    @Test
    public void testRun() throws HttpBasicException, UnsupportedEncodingException {
        String credentials = "user:password";
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpBasicEntity actual = subject.run("Basic " + encodedCredentials);
        assertThat(actual.getUser(), is("user"));
        assertThat(actual.getPassword(), is("password"));
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderIsEmpty() throws HttpBasicException {
        subject.run("");
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderIsNull() throws HttpBasicException {
        subject.run(null);
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderIsNotBasic() throws HttpBasicException {
        subject.run("foo");
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderMissingCredentials() throws HttpBasicException {
        subject.run("Basic ");
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderHasNoColon() throws HttpBasicException {
        String garbage = Base64.getEncoder().encodeToString("gabage".getBytes());
        subject.run("Basic " + garbage);
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderMissingPassword() throws HttpBasicException {
        String missingPassword = Base64.getEncoder().encodeToString("user:".getBytes());
        subject.run("Basic " + missingPassword);
    }

}
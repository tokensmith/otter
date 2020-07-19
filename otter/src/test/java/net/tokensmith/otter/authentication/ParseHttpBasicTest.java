package net.tokensmith.otter.authentication;

import net.tokensmith.otter.authentication.exception.HttpBasicException;
import org.junit.Before;
import org.junit.Test;

import java.util.Base64;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class ParseHttpBasicTest {

    private ParseHttpBasic subject;

    @Before
    public void setUp() {
        subject = new ParseHttpBasic();
    }

    @Test
    public void testRun() throws Exception {
        String credentials = "user:password";
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpBasicEntity actual = subject.run("Basic " + encodedCredentials);
        assertThat(actual.getUser(), is("user"));
        assertThat(actual.getPassword(), is("password"));
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderIsEmpty() throws Exception {
        subject.run("");
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderIsNull() throws Exception {
        subject.run(null);
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderIsNotBasic() throws Exception {
        subject.run("foo");
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderMissingCredentials() throws Exception {
        subject.run("Basic ");
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderHasNoColon() throws Exception {
        String garbage = Base64.getEncoder().encodeToString("garbage".getBytes());
        subject.run("Basic " + garbage);
    }

    @Test(expected=HttpBasicException.class)
    public void testHeaderMissingPassword() throws Exception {
        String missingPassword = Base64.getEncoder().encodeToString("user:".getBytes());
        subject.run("Basic " + missingPassword);
    }

}
package net.tokensmith.otter.router;

import helper.fake.FakeServlet;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class GetServletURITest {

    private static String BASE_URI = "https://tokensmith.net";
    private GetServletURI subject;

    @Before
    public void setUp() {
        subject = new GetServletURI();
    }

    @Test
    public void testRun() throws Exception {
        String actual = subject.run(BASE_URI, FakeServlet.class);
        assertThat(actual, is(BASE_URI + "/fake"));
    }

    @Test
    public void testRunRemovForwardSlash() throws Exception {
        String actual = subject.run(BASE_URI + "/", FakeServlet.class);
        assertThat(actual, is(BASE_URI + "/fake"));
    }
}

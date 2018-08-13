package org.rootservices.otter.router;

import helper.entity.FakeServlet;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class GetServletURITest {

    private static String BASE_URI = "https://rootservices.org";
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

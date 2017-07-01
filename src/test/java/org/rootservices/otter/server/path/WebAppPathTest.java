package org.rootservices.otter.server.path;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import suite.UnitTest;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@Category(UnitTest.class)
public class WebAppPathTest {

    private CompiledClassPath compiledClassPath;
    private WebAppPath subject;

    @Before
    public void setUp() throws Exception {
        compiledClassPath = new CompiledClassPath();
        subject = new WebAppPath();
    }

    @Test
    public void fromClassUriShouldReturnPathToWebApp() throws Exception {
        URI classPath = compiledClassPath.getForClass(CompiledClassPath.class);

        URI actual = subject.fromClassURI(classPath);
        String actualPath = actual.getPath();

        assertThat(actualPath.endsWith("/otter/src/main/webapp"), is(true));
    }
}
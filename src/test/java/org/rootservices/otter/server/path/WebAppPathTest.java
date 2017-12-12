package org.rootservices.otter.server.path;

import integration.app.hello.controller.HelloResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import suite.UnitTest;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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

        String expected = "/otter/src/main/webapp";
        String errorMsg = "the class uri " + classPath + " was changed to " + actualPath + " and it does not end with " + expected;
        assertThat(errorMsg, actualPath.endsWith(expected), is(true));
    }

    @Test
    public void fromClassUriWhenMultBuildDirsShouldReturnPathToWebApp() throws Exception {
        URI classPath = new URI("file:/home/travis/build/RootServices/otter/build/classes/java/main/");

        URI actual = subject.fromClassURI(classPath);
        String actualPath = actual.getPath();

        String expected = "/home/travis/build/RootServices/otter/src/main/webapp";
        String errorMsg = actualPath + " does not match " + expected;
        assertThat(errorMsg, actualPath, is(expected));
    }

    @Test
    public void fromClassUriWithCustomLocationWhenMultBuildDirsShouldReturnPathToWebApp() throws Exception {
        URI classPath = new URI("file:/home/travis/build/RootServices/otter/build/classes/java/main/");

        URI actual = subject.fromClassURI(classPath, "/integration/app/webapp");
        String actualPath = actual.getPath();

        String expected = "/home/travis/build/RootServices/otter/integration/app/webapp";
        String errorMsg = actualPath + " does not match " + expected;
        assertThat(errorMsg, actualPath, is(expected));
    }
}
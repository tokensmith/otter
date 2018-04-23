package org.rootservices.otter.server.path;


import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;



public class CompiledClassPathTest {

    private CompiledClassPath subject;

    @Before
    public void setUp() throws Exception {
        subject = new CompiledClassPath();
    }

    @Test
    public void getForClassShouldReturnPathToCompliedClasses() throws Exception {
        URI actual = subject.getForClass(WebAppPath.class);

        String actualPath = actual.getPath();

        String expected = "/otter/build/classes/java/main/";
        assertThat(actualPath + " does not end with " + expected, actualPath.endsWith(expected), is(true));
    }
}
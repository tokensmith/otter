package org.rootservices.otter.server.path;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 4/5/16.
 */
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
        assertThat(actualPath.endsWith("/otter/target/classes/"), is(true));
    }
}
package net.tokensmith.otter.controller.entity;

import net.tokensmith.otter.controller.entity.request.Request;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestTest {

    @Test
    public void getSessionIsEmpty() {
        Request request = new Request();
        assertThat(request.getSession(), is(notNullValue()));
        assertThat(request.getSession().isPresent(), is(false));
    }
}
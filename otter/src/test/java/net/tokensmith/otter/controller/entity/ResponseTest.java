package net.tokensmith.otter.controller.entity;

import net.tokensmith.otter.controller.entity.response.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ResponseTest {

    @Test
    public void getSessionIsEmpty() {
        Response response = new Response();
        assertThat(response.getSession(), is(notNullValue()));
        assertThat(response.getSession().isPresent(), is(false));
    }
}
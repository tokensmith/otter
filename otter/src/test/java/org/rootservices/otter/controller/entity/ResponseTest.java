package org.rootservices.otter.controller.entity;

import org.junit.Test;
import org.rootservices.otter.controller.entity.response.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class ResponseTest {

    @Test
    public void getSessionIsEmpty() {
        Response response = new Response();
        assertThat(response.getSession(), is(notNullValue()));
        assertThat(response.getSession().isPresent(), is(false));
    }
}
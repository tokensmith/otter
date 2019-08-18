package org.rootservices.otter.controller.entity;

import org.junit.Test;
import org.rootservices.otter.controller.entity.request.Request;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class RequestTest {

    @Test
    public void getSessionIsEmpty() {
        Request request = new Request();
        assertThat(request.getSession(), is(notNullValue()));
        assertThat(request.getSession().isPresent(), is(false));
    }
}
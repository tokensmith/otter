package net.tokensmith.otter.gateway.servlet;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GatewayResponseTest {

    @Test
    public void whenConstructedShouldDefaultEmpty() {
        GatewayResponse actual = new GatewayResponse();

        assertThat(actual.getPayload(), is(notNullValue()));
        assertThat(actual.getPayload().isPresent(), is(false));
        assertThat(actual.getTemplate().isPresent(), is(false));
    }

}
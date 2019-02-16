package org.rootservices.otter.gateway.builder;


import helper.entity.DummyRestBetween;
import helper.entity.DummyUser;
import org.junit.Test;
import org.rootservices.otter.gateway.entity.RestGroup;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class RestGroupBuilderTest {

    @Test
    public void buildShouldHaveEmptyAuthBetweens() {

        RestGroup<DummyUser> actual = new RestGroupBuilder<DummyUser>()
                .name("API")
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getAuthOptional(), is(notNullValue()));
        assertThat(actual.getAuthOptional().isPresent(), is(false));
        assertThat(actual.getAuthRequired(), is(notNullValue()));
        assertThat(actual.getAuthRequired().isPresent(), is(false));
    }

    @Test
    public void buildShouldHaveAuthBetweens() {

        DummyRestBetween<DummyUser> authRequired = new DummyRestBetween<DummyUser>();
        DummyRestBetween<DummyUser> authOptional = new DummyRestBetween<DummyUser>();

        RestGroup<DummyUser> actual = new RestGroupBuilder<DummyUser>()
                .name("API")
                .authRequired(authRequired)
                .authOptional(authOptional)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getAuthOptional(), is(notNullValue()));
        assertThat(actual.getAuthOptional().isPresent(), is(true));
        assertThat(actual.getAuthOptional().get(), is(authOptional));
        assertThat(actual.getAuthRequired(), is(notNullValue()));
        assertThat(actual.getAuthRequired().isPresent(), is(true));
        assertThat(actual.getAuthRequired().get(), is(authRequired));
    }
}
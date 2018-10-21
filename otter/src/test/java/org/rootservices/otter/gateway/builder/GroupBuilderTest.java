package org.rootservices.otter.gateway.builder;

import helper.entity.DummyBetween;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Test;
import org.rootservices.otter.gateway.entity.Group;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class GroupBuilderTest {

    @Test
    public void buildShouldHaveEmptyAuthBetweens() {

        Group<DummySession, DummyUser> actual = new GroupBuilder<DummySession, DummyUser>()
                .name("API")
                .sessionClazz(DummySession.class)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getSessionClazz(), is(notNullValue()));
        assertThat(actual.getAuthOptional(), is(notNullValue()));
        assertThat(actual.getAuthOptional().isPresent(), is(false));
        assertThat(actual.getAuthRequired(), is(notNullValue()));
        assertThat(actual.getAuthRequired().isPresent(), is(false));
    }

    @Test
    public void buildShouldHaveAuthBetweens() {

        DummyBetween<DummySession, DummyUser> authRequired = new DummyBetween<DummySession, DummyUser>();
        DummyBetween<DummySession, DummyUser> authOptional = new DummyBetween<DummySession, DummyUser>();

        Group<DummySession, DummyUser> actual = new GroupBuilder<DummySession, DummyUser>()
                .name("API")
                .sessionClazz(DummySession.class)
                .authRequired(authRequired)
                .authOptional(authOptional)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getSessionClazz(), is(notNullValue()));
        assertThat(actual.getAuthOptional(), is(notNullValue()));
        assertThat(actual.getAuthOptional().isPresent(), is(true));
        assertThat(actual.getAuthOptional().get(), is(authOptional));
        assertThat(actual.getAuthRequired(), is(notNullValue()));
        assertThat(actual.getAuthRequired().isPresent(), is(true));
        assertThat(actual.getAuthRequired().get(), is(authRequired));
    }
}
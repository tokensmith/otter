package org.rootservices.otter.gateway.builder;

import helper.entity.DummyBetween;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.ServerErrorResource;
import org.junit.Test;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.gateway.entity.Group;

import static org.hamcrest.Matchers.is;
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
        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(0));
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
        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(0));
    }


    @Test
    public void buildShouldHaveErrorResources() {

        DummyBetween<DummySession, DummyUser> authRequired = new DummyBetween<DummySession, DummyUser>();
        DummyBetween<DummySession, DummyUser> authOptional = new DummyBetween<DummySession, DummyUser>();

        ServerErrorResource serverErrorResource = new ServerErrorResource();

        Group<DummySession, DummyUser> actual = new GroupBuilder<DummySession, DummyUser>()
                .name("API")
                .sessionClazz(DummySession.class)
                .authRequired(authRequired)
                .authOptional(authOptional)
                .errorResource(StatusCode.SERVER_ERROR, serverErrorResource)
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
        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(1));
        assertThat(actual.getErrorResources().get(StatusCode.SERVER_ERROR), is(serverErrorResource));
    }
}
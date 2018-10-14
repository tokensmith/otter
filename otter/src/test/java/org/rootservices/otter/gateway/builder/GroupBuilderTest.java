package org.rootservices.otter.gateway.builder;

import helper.entity.DummySession;
import org.junit.Test;
import org.rootservices.otter.gateway.entity.Group;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class GroupBuilderTest {

    @Test
    public void buildShouldBeOk() {

        Group<DummySession> actual = new GroupBuilder<DummySession>()
                .name("API")
                .sessionClazz(DummySession.class)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getSessionClazz(), is(notNullValue()));
    }
}
package org.rootservices.otter.router.builder;

import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.FakeResource;
import org.junit.Test;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class RouteBuilderTest {

    @Test
    public void buildShouldBeOk() {
        FakeResource resource = new FakeResource();

        Route<DummySession, DummyUser> actual = new RouteBuilder<DummySession, DummyUser>()
                .resource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResource(), is(resource));
        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
    }


    @Test
    public void buildWhenBetweenShouldAssign() {
        FakeResource resource = new FakeResource();
        List<Between<DummySession, DummyUser>> before = new ArrayList<>();
        List<Between<DummySession, DummyUser>> after = new ArrayList<>();

        Route<DummySession, DummyUser> actual = new RouteBuilder<DummySession, DummyUser>()
                .resource(resource)
                .before(after)
                .after(before)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResource(), is(resource));
        assertThat(actual.getBefore(), is(before));
        assertThat(actual.getAfter(), is(after));
    }
}
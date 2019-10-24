package net.tokensmith.otter.router.builder;

import helper.entity.model.DummyPayload;
import helper.entity.model.DummyUser;
import helper.entity.OkRestResource;
import org.junit.Test;
import net.tokensmith.otter.router.entity.RestRoute;
import net.tokensmith.otter.router.entity.between.RestBetween;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;


public class RestRouteBuilderTest {

    @Test
    public void buildShouldBeOk() {
        OkRestResource resource = new OkRestResource();

        RestRoute<DummyUser, DummyPayload> actual = new RestRouteBuilder<DummyUser, DummyPayload>()
                .restResource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRestResource(), is(resource));
        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
    }


    @Test
    public void buildWhenBetweenShouldAssign() {
        OkRestResource resource = new OkRestResource();
        List<RestBetween<DummyUser>> before = new ArrayList<>();
        List<RestBetween<DummyUser>> after = new ArrayList<>();

        RestRoute<DummyUser, DummyPayload> actual = new RestRouteBuilder<DummyUser, DummyPayload>()
                .restResource(resource)
                .before(after)
                .after(before)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRestResource(), is(resource));
        assertThat(actual.getBefore(), is(before));
        assertThat(actual.getAfter(), is(after));
    }

}
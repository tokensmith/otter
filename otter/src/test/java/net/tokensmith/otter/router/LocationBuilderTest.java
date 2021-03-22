package net.tokensmith.otter.router;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import helper.fake.FakeResource;
import net.tokensmith.otter.controller.builder.MimeTypeBuilder;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.router.builder.LocationBuilder;
import net.tokensmith.otter.router.entity.Location;
import net.tokensmith.otter.router.entity.Route;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class LocationBuilderTest {
    private LocationBuilder<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        subject = new LocationBuilder<DummySession, DummyUser>();
    }

    @Test
    public void pathShouldBeOK() {
        String regex = "/foo/(.*)";

        Location actual = subject.path(regex).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
    }

    @Test
    public void resourceShouldBeOK() {
        FakeResource resource = new FakeResource();

        Location actual = subject.resource(resource).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getRouteRunner(), is(notNullValue()));

        assertThat(actual.getErrorRouteRunners(), is(notNullValue()));
        assertThat(actual.getErrorRouteRunners().size(), is(0));
    }

    @Test
    public void pathAndResourceAndContentTypesShouldBeOK() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        Location actual = subject.path(regex)
                .contentTypes(contentTypes)
                .accepts(contentTypes)
                .resource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRouteRunner(), is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
        assertThat(actual.getContentTypes(), is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getAccepts(), is(notNullValue()));
        assertThat(actual.getAccepts().size(), is(0));

        assertThat(actual.getErrorRouteRunners(), is(notNullValue()));
        assertThat(actual.getErrorRouteRunners().size(), is(0));
    }

    @Test
    public void pathAndResourceAndContentTypeShouldBeOK() {
        String regex = "/foo/(.*)";
        MimeType html = new MimeTypeBuilder().html().build();
        FakeResource resource = new FakeResource();

        Location actual = subject.path(regex)
                .contentType(html)
                .accept(html)
                .resource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRouteRunner(), is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
        assertThat(actual.getContentTypes(), is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(1));
        assertThat(actual.getAccepts(), is(notNullValue()));
        assertThat(actual.getAccepts().size(), is(1));

        assertThat(actual.getErrorRouteRunners(), is(notNullValue()));
        assertThat(actual.getErrorRouteRunners().size(), is(0));
    }

    @Test
    public void errorRouteRunnerShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        FakeResource errorResource = new FakeResource();

        Location actual = subject.path(regex)
                .contentTypes(contentTypes)
                .accepts(contentTypes)
                .resource(resource)
                .errorRouteRunner(StatusCode.NOT_FOUND, errorResource)
                .build();

        assertThat(actual.getErrorRouteRunners().size(), is(1));
        assertThat(actual.getErrorRouteRunners().get(StatusCode.NOT_FOUND), is(notNullValue()));
    }

    @Test
    public void errorRouteRunnersShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Location actual = subject.path(regex)
                .contentTypes(contentTypes)
                .accepts(contentTypes)
                .resource(resource)
                .errorRouteRunners(errorRoutes)
                .build();

        assertThat(actual.getErrorRouteRunners().size(), is(3));
    }

}
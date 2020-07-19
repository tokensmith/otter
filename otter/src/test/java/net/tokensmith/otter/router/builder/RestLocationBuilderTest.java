package net.tokensmith.otter.router.builder;

import helper.entity.OkRestResource;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.builder.MimeTypeBuilder;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.controller.error.rest.NotFoundRestResource;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.dispatch.json.JsonDispatchErrorRouteRun;
import net.tokensmith.otter.dispatch.json.JsonRouteRun;
import net.tokensmith.otter.dispatch.translator.RestErrorHandler;
import net.tokensmith.otter.router.entity.Location;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class RestLocationBuilderTest {
    private RestLocationBuilder<DummySession, DummyUser, DummyPayload> subject;

    @Before
    public void setUp() {
        subject = new RestLocationBuilder<DummySession, DummyUser, DummyPayload>();
    }

    @Test
    public void pathShouldBeOK() {
        String regex = "/foo/(.*)";

        Location actual = subject
                .payload(DummyPayload.class)
                .path(regex)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRouteRunner(), is(notNullValue()));
        assertThat(actual.getRouteRunner(), instanceOf(JsonRouteRun.class));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
    }

    @Test
    public void resourceShouldBeOK() {
        OkRestResource resource = new OkRestResource();

        Location actual = subject
                .payload(DummyPayload.class)
                .restResource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getRouteRunner(), is(notNullValue()));
        assertThat(actual.getRouteRunner(), instanceOf(JsonRouteRun.class));

        assertThat(actual.getErrorRouteRunners(), is(notNullValue()));
        assertThat(actual.getErrorRouteRunners().size(), is(0));
    }

    @Test
    public void pathAndResourceAndContentTypesShouldBeOK() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        OkRestResource resource = new OkRestResource();

        Location actual = subject
                .payload(DummyPayload.class)
                .path(regex)
                .contentTypes(contentTypes)
                .accepts(contentTypes)
                .restResource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRouteRunner(), is(notNullValue()));
        assertThat(actual.getRouteRunner(), instanceOf(JsonRouteRun.class));
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
        MimeType json = new MimeTypeBuilder().json().build();
        OkRestResource resource = new OkRestResource();

        Location actual = subject
                .payload(DummyPayload.class)
                .path(regex)
                .contentType(json)
                .accept(json)
                .restResource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRouteRunner(), is(notNullValue()));
        assertThat(actual.getRouteRunner(), instanceOf(JsonRouteRun.class));
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
        OkRestResource resource = new OkRestResource();

        RouteRunner errorRouteRunner = new JsonRouteRun<DummySession, DummyUser, DummyPayload>();

        Location actual = subject
                .payload(DummyPayload.class)
                .path(regex)
                .contentTypes(contentTypes)
                .accepts(contentTypes)
                .restResource(resource)
                .errorRouteRunner(StatusCode.NOT_FOUND, errorRouteRunner)
                .build();

        assertThat(actual.getErrorRouteRunners().size(), is(1));
        assertThat(actual.getErrorRouteRunners().get(StatusCode.NOT_FOUND), is(notNullValue()));
        assertThat(actual.getRouteRunner(), instanceOf(JsonRouteRun.class));
    }

    @Test
    public void errorHandlersShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        OkRestResource resource = new OkRestResource();

        Map<StatusCode, RestErrorHandler<DummyUser>> errorHandlers = new HashMap<>();

        Location actual = subject
            .payload(DummyPayload.class)
            .path(regex)
            .contentTypes(contentTypes)
            .accepts(contentTypes)
            .restResource(resource)
            .restErrorHandlers(errorHandlers)
            .build();

        // no interface exposed to ensure it was built accurately.
        // this is good enough for now
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRouteRunner(), instanceOf(JsonRouteRun.class));
    }

    @Test
    public void dispatchErrorShouldMakeJsonDispatchErrorRouteRun() {
        RestLocationBuilder<DummySession, DummyUser, ClientError> subject = new RestLocationBuilder<DummySession, DummyUser, ClientError>();;
        NotFoundRestResource<DummyUser> resource = new NotFoundRestResource<DummyUser>();

        Location actual = subject
                .payload(ClientError.class)
                .restResource(resource)
                .isDispatchError(true)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getRouteRunner(), is(notNullValue()));
        assertThat(actual.getRouteRunner(), instanceOf(JsonDispatchErrorRouteRun.class));

        assertThat(actual.getErrorRouteRunners(), is(notNullValue()));
        assertThat(actual.getErrorRouteRunners().size(), is(0));
    }
}
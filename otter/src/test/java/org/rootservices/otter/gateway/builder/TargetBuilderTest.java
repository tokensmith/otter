package org.rootservices.otter.gateway.builder;


import helper.entity.DummyBetween;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.ServerErrorResource;
import helper.fake.FakeResourceLegacy;
import org.junit.Test;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.router.entity.Method;



import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class TargetBuilderTest {

    public TargetBuilder<DummySession, DummyUser> subject() {
        return new TargetBuilder<DummySession, DummyUser>();
    }

    @Test
    public void buildShouldHaveEmptyLists() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        Target<DummySession, DummyUser> actual = subject.build();

        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getMethods().size(), is(0));
        assertThat(actual.getLabels().size(), is(0));
        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildShouldBeOk() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        FakeResourceLegacy notFoundResource = new FakeResourceLegacy();
        ErrorTarget<DummySession, DummyUser> notFound = new ErrorTargetBuilder<DummySession, DummyUser>()
                .resource(notFoundResource)
                .build();

        FakeResourceLegacy fakeResource = new FakeResourceLegacy();
        MimeType json = new MimeTypeBuilder().json().build();

        Target<DummySession, DummyUser> actual = subject
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(json)
                .resource(fakeResource)
                .before(new DummyBetween<>())
                .before(new DummyBetween<>())
                .after(new DummyBetween<>())
                .after(new DummyBetween<>())
                .label(Label.CSRF)
                .label(Label.SESSION_REQUIRED)
                .errorTarget(StatusCode.NOT_FOUND, notFound)
                .build();

        assertThat(actual.getRegex(), is("/foo"));
        assertThat(actual.getMethods().get(0), is(Method.GET));
        assertThat(actual.getMethods().get(1), is(Method.POST));
        assertThat(actual.getResource(), is(fakeResource));
        assertThat(actual.getContentTypes().size(), is(9));
        assertThat(actual.getContentTypes().get(Method.GET).get(0), is(json));
        assertThat(actual.getContentTypes().get(Method.POST).get(0), is(json));
        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getLabels().size(), is(2));
        assertThat(actual.getLabels().get(0), is(Label.CSRF));
        assertThat(actual.getLabels().get(1), is(Label.SESSION_REQUIRED));
        assertThat(actual.getErrorTargets().size(), is(1));
        assertThat(actual.getErrorTargets().get(StatusCode.NOT_FOUND).getResource(), is(notFoundResource));
        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(0));
    }

    @Test
    public void buildWhenMethodContentTypeShouldBeOk() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        FakeResourceLegacy notFoundResource = new FakeResourceLegacy();
        ErrorTarget<DummySession, DummyUser> notFound = new ErrorTargetBuilder<DummySession, DummyUser>()
                .resource(notFoundResource)
                .build();

        FakeResourceLegacy fakeResource = new FakeResourceLegacy();
        MimeType json = new MimeTypeBuilder().json().build();
        MimeType jwt = new MimeTypeBuilder().jwt().build();

        Target<DummySession, DummyUser> actual = subject
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(Method.GET, jwt)
                .contentType(json)
                .resource(fakeResource)
                .before(new DummyBetween<>())
                .before(new DummyBetween<>())
                .after(new DummyBetween<>())
                .after(new DummyBetween<>())
                .label(Label.CSRF)
                .label(Label.SESSION_REQUIRED)
                .errorTarget(StatusCode.NOT_FOUND, notFound)
                .build();


        assertThat(actual.getContentTypes().size(), is(9));
        assertThat(actual.getContentTypes().get(Method.GET).get(0), is(jwt));
        assertThat(actual.getContentTypes().get(Method.GET).get(1), is(json));
        assertThat(actual.getContentTypes().get(Method.POST).get(0), is(json));
    }

    @Test
    public void buildShouldHaveErrorResources() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        FakeResourceLegacy notFoundResource = new FakeResourceLegacy();
        ErrorTarget<DummySession, DummyUser> notFound = new ErrorTargetBuilder<DummySession, DummyUser>()
                .resource(notFoundResource)
                .build();

        FakeResourceLegacy fakeResource = new FakeResourceLegacy();
        MimeType json = new MimeTypeBuilder().json().build();

        ServerErrorResource serverErrorResource = new ServerErrorResource();

        Target<DummySession, DummyUser> actual = subject
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(json)
                .resource(fakeResource)
                .before(new DummyBetween<>())
                .before(new DummyBetween<>())
                .after(new DummyBetween<>())
                .after(new DummyBetween<>())
                .label(Label.CSRF)
                .label(Label.SESSION_REQUIRED)
                .errorTarget(StatusCode.NOT_FOUND, notFound)
                .onError(StatusCode.SERVER_ERROR, serverErrorResource)
                .build();

        assertThat(actual.getRegex(), is("/foo"));
        assertThat(actual.getMethods().get(0), is(Method.GET));
        assertThat(actual.getMethods().get(1), is(Method.POST));
        assertThat(actual.getResource(), is(fakeResource));
        assertThat(actual.getContentTypes().size(), is(9));
        assertThat(actual.getContentTypes().get(Method.GET).get(0), is(json));
        assertThat(actual.getContentTypes().get(Method.POST).get(0), is(json));
        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getLabels().size(), is(2));
        assertThat(actual.getLabels().get(0), is(Label.CSRF));
        assertThat(actual.getLabels().get(1), is(Label.SESSION_REQUIRED));
        assertThat(actual.getErrorTargets().size(), is(1));
        assertThat(actual.getErrorTargets().get(StatusCode.NOT_FOUND).getResource(), is(notFoundResource));
        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(1));
        assertThat(actual.getErrorResources().get(StatusCode.SERVER_ERROR), is(serverErrorResource));
    }

}
package org.rootservices.otter.gateway.builder;


import helper.entity.DummyBetween;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import helper.entity.ServerErrorResource;
import helper.fake.FakeResource;
import org.hamcrest.core.IsNull;
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
    public void buildShouldHaveEmptyListsAndDefaultOptionalForAuthenticate() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        Target<DummySession, DummyUser> actual = subject.build();

        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getAccepts().size(), is(0));
        assertThat(actual.getMethods().size(), is(0));
        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getLabels().size(), is(2));
        assertTrue(actual.getLabels().contains(Label.SESSION_OPTIONAL));
        assertTrue(actual.getLabels().contains(Label.AUTH_OPTIONAL));
    }

    @Test
    public void buildWhenFormShouldHaveMethodsAndCSRF() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        FakeResource fakeResource = new FakeResource();

        Target<DummySession, DummyUser> actual = subject
                .regex("/foo")
                .form()
                .resource(fakeResource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(IsNull.notNullValue()));

        assertThat(actual.getLabels(), is(IsNull.notNullValue()));
        assertThat(actual.getLabels().size(), is(3));
        assertTrue(actual.getLabels().contains(Label.CSRF));

        assertThat(actual.getMethods(), is(IsNull.notNullValue()));
        assertThat(actual.getMethods().size(), is(2));
        assertTrue(actual.getMethods().contains(Method.GET));

    }

    @Test
    public void buildShouldBeOk() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        FakeResource notFoundResource = new FakeResource();
        ErrorTarget<DummySession, DummyUser> notFound = new ErrorTargetBuilder<DummySession, DummyUser>()
                .resource(notFoundResource)
                .build();

        FakeResource fakeResource = new FakeResource();
        MimeType html = new MimeTypeBuilder().html().build();

        Target<DummySession, DummyUser> actual = subject
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(html)
                .accept(html)
                .resource(fakeResource)
                .before(new DummyBetween<>())
                .before(new DummyBetween<>())
                .after(new DummyBetween<>())
                .after(new DummyBetween<>())
                .onDispatchError(StatusCode.NOT_FOUND, notFound)
                .build();

        assertThat(actual.getRegex(), is("/foo"));
        assertThat(actual.getMethods().get(0), is(Method.GET));
        assertThat(actual.getMethods().get(1), is(Method.POST));
        assertThat(actual.getResource(), is(fakeResource));
        assertThat(actual.getContentTypes().size(), is(9));
        assertThat(actual.getContentTypes().get(Method.GET).get(0), is(html));
        assertThat(actual.getContentTypes().get(Method.POST).get(0), is(html));
        assertThat(actual.getAccepts().size(), is(9));
        assertThat(actual.getAccepts().get(Method.GET).get(0), is(html));
        assertThat(actual.getAccepts().get(Method.POST).get(0), is(html));
        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(2));

        assertThat(actual.getLabels().size(), is(2));
        assertTrue(actual.getLabels().contains(Label.SESSION_OPTIONAL));
        assertTrue(actual.getLabels().contains(Label.AUTH_OPTIONAL));

        assertThat(actual.getErrorTargets().size(), is(1));
        assertThat(actual.getErrorTargets().get(StatusCode.NOT_FOUND).getResource(), is(notFoundResource));
        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(0));
    }

    @Test
    public void buildWhenMethodContentAndAcceptTypeShouldBeOk() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        FakeResource notFoundResource = new FakeResource();
        ErrorTarget<DummySession, DummyUser> notFound = new ErrorTargetBuilder<DummySession, DummyUser>()
                .resource(notFoundResource)
                .build();

        FakeResource fakeResource = new FakeResource();
        MimeType json = new MimeTypeBuilder().json().build();
        MimeType jwt = new MimeTypeBuilder().jwt().build();

        Target<DummySession, DummyUser> actual = subject
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(Method.GET, json)
                .contentType(Method.POST, json)
                .accept(Method.GET, jwt)
                .accept(Method.POST, json)
                .resource(fakeResource)
                .before(new DummyBetween<>())
                .before(new DummyBetween<>())
                .after(new DummyBetween<>())
                .after(new DummyBetween<>())
                .form()
                .onDispatchError(StatusCode.NOT_FOUND, notFound)
                .build();


        assertThat(actual.getContentTypes().size(), is(2));
        assertThat(actual.getContentTypes().get(Method.GET).get(0), is(json));
        assertThat(actual.getContentTypes().get(Method.POST).get(0), is(json));

        assertThat(actual.getAccepts().get(Method.GET).get(0), is(jwt));
        assertThat(actual.getAccepts().get(Method.POST).get(0), is(json));
    }

    @Test
    public void buildShouldHaveErrorResources() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        FakeResource notFoundResource = new FakeResource();
        ErrorTarget<DummySession, DummyUser> notFound = new ErrorTargetBuilder<DummySession, DummyUser>()
                .resource(notFoundResource)
                .build();

        FakeResource fakeResource = new FakeResource();
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
                .onDispatchError(StatusCode.NOT_FOUND, notFound)
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

        assertThat(actual.getErrorTargets().size(), is(1));
        assertThat(actual.getErrorTargets().get(StatusCode.NOT_FOUND).getResource(), is(notFoundResource));
        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(1));
        assertThat(actual.getErrorResources().get(StatusCode.SERVER_ERROR), is(serverErrorResource));
    }


    @Test
    public void buildAuthenticateShouldHaveLabels() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        Target<DummySession, DummyUser> actual = subject
                .authenticate()
                .build();

        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getMethods().size(), is(0));
        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getLabels().size(), is(2));
        assertTrue(actual.getLabels().contains(Label.AUTH_REQUIRED));
        assertTrue(actual.getLabels().contains(Label.SESSION_REQUIRED));
    }

    @Test
    public void buildAnonymousShouldHaveNoLabels() {
        TargetBuilder<DummySession, DummyUser> subject = subject();

        Target<DummySession, DummyUser> actual = subject
                .anonymous()
                .build();

        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getMethods().size(), is(0));
        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getLabels().size(), is(0));
    }

}
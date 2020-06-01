package net.tokensmith.otter.gateway.builder;

import helper.entity.*;
import helper.entity.model.DummyErrorPayload;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import helper.fake.FakeValidate;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import org.junit.Test;
import net.tokensmith.otter.controller.builder.MimeTypeBuilder;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.gateway.entity.*;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.entity.rest.RestTarget;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.translatable.Translatable;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class RestTargetBuilderTest {

    public RestTargetBuilder<DummySession, DummyUser, DummyPayload> subject() {
        return  new RestTargetBuilder<>();
    }

    @Test
    public void buildShouldHaveEmptyLists() {
        RestTargetBuilder<DummySession, DummyUser, DummyPayload> subject = subject();

        RestTarget<DummySession, DummyUser, DummyPayload> actual = subject.build();

        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getAccepts().size(), is(0));
        assertThat(actual.getMethods().size(), is(0));
        assertThat(actual.getLabels().size(), is(1));
        assertTrue(actual.getLabels().contains(Label.AUTH_OPTIONAL));
        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildWhenAnonymousShouldHaveNoLabels() {
        RestTargetBuilder<DummySession, DummyUser, DummyPayload> subject = subject();

        RestTarget<DummySession, DummyUser, DummyPayload> actual = subject
                .anonymous()
                .build();

        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getAccepts().size(), is(0));
        assertThat(actual.getMethods().size(), is(0));
        assertThat(actual.getLabels().size(), is(0));
        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildShouldHaveRestErrors() {

        ClientErrorRestResource errorRestResource = new ClientErrorRestResource();

        RestTargetBuilder<DummySession, DummyUser, DummyPayload> subject = subject();
        RestTarget<DummySession, DummyUser, DummyPayload> actual = subject
                .onError(StatusCode.BAD_REQUEST, errorRestResource, DummyErrorPayload.class)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRestErrors(), is(notNullValue()));
        assertThat(actual.getRestErrors().size(), is(1));

        assertThat(actual.getRestErrors().get(StatusCode.BAD_REQUEST), is(notNullValue()));

        Class<DummyErrorPayload> payload = to(actual.getRestErrors().get(StatusCode.BAD_REQUEST).getPayload());
        assertThat(payload, is(notNullValue()));

        assertThat(actual.getRestErrors().get(StatusCode.BAD_REQUEST).getRestResource(), is(errorRestResource));
    }

    @SuppressWarnings("unchecked")
    public Class<DummyErrorPayload> to(Class<? extends Translatable> from) {
        return (Class<DummyErrorPayload>) from;
    }

    @Test
    public void buildCrudShouldAddMethods() {
        RestTargetBuilder<DummySession, DummyUser, DummyPayload> subject = subject();

        MimeType json = new MimeTypeBuilder().json().build();
        OkRestResource okRestResource = new OkRestResource();

        RestTarget<DummySession, DummyUser, DummyPayload> actual = subject
                .regex("/foo")
                .crud()
                .restResource(okRestResource)
                .authenticate()
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethods(), is(notNullValue()));
        assertThat(actual.getMethods().size(), is(5));
        assertTrue(actual.getMethods().contains(Method.GET));
        assertTrue(actual.getMethods().contains(Method.PUT));
        assertTrue(actual.getMethods().contains(Method.PATCH));
        assertTrue(actual.getMethods().contains(Method.POST));
        assertTrue(actual.getMethods().contains(Method.DELETE));

        assertThat(actual.getContentTypes().size(), is(5));
        assertThat(actual.getContentTypes().get(Method.GET).size(), is(1));
        assertThat(actual.getContentTypes().get(Method.GET).get(0), is(json));
        assertThat(actual.getContentTypes().get(Method.PUT).size(), is(1));
        assertThat(actual.getContentTypes().get(Method.PUT).get(0), is(json));
        assertThat(actual.getContentTypes().get(Method.PATCH).size(), is(1));
        assertThat(actual.getContentTypes().get(Method.PATCH).get(0), is(json));
        assertThat(actual.getContentTypes().get(Method.POST).size(), is(1));
        assertThat(actual.getContentTypes().get(Method.POST).get(0), is(json));
        assertThat(actual.getContentTypes().get(Method.DELETE).size(), is(1));
        assertThat(actual.getContentTypes().get(Method.DELETE).get(0), is(json));

        assertThat(actual.getAccepts().size(), is(5));
        assertThat(actual.getAccepts().get(Method.GET).size(), is(1));
        assertThat(actual.getAccepts().get(Method.GET).get(0), is(json));
        assertThat(actual.getAccepts().get(Method.PUT).size(), is(1));
        assertThat(actual.getAccepts().get(Method.PUT).get(0), is(json));
        assertThat(actual.getAccepts().get(Method.PATCH).size(), is(1));
        assertThat(actual.getAccepts().get(Method.PATCH).get(0), is(json));
        assertThat(actual.getAccepts().get(Method.POST).size(), is(1));
        assertThat(actual.getAccepts().get(Method.POST).get(0), is(json));
        assertThat(actual.getAccepts().get(Method.DELETE).size(), is(1));
        assertThat(actual.getAccepts().get(Method.DELETE).get(0), is(json));
    }

    @Test
    public void buildShouldBeOk() {
        RestTargetBuilder<DummySession, DummyUser, DummyPayload> subject = subject();

        OkRestResource notFoundResource = new OkRestResource();
        RestErrorTarget<DummySession, DummyUser, DummyPayload> notFound = new RestErrorTarget<>(
                DummyPayload.class, notFoundResource, new ArrayList<>(), new ArrayList<>()
        );

        OkRestResource okRestResource = new OkRestResource();
        MimeType json = new MimeTypeBuilder().json().build();
        Validate validate = new FakeValidate();

        RestTarget<DummySession, DummyUser, DummyPayload> actual = subject
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(json)
                .accept(json)
                .restResource(okRestResource)
                .before(new DummyRestBetween<>())
                .before(new DummyRestBetween<>())
                .after(new DummyRestBetween<>())
                .after(new DummyRestBetween<>())
                .authenticate()
                .onDispatchError(StatusCode.NOT_FOUND, notFound)
                .validate(validate)
                .build();

        assertThat(actual.getRegex(), is("/foo"));
        assertThat(actual.getMethods().get(0), is(Method.GET));
        assertThat(actual.getMethods().get(1), is(Method.POST));
        assertThat(actual.getRestResource(), is(okRestResource));
        assertThat(actual.getContentTypes().size(), is(9));
        assertThat(actual.getContentTypes().get(Method.GET).get(0), is(json));
        assertThat(actual.getContentTypes().get(Method.POST).get(0), is(json));
        assertThat(actual.getAccepts().size(), is(9));
        assertThat(actual.getAccepts().get(Method.GET).get(0), is(json));
        assertThat(actual.getAccepts().get(Method.POST).get(0), is(json));
        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getLabels().size(), is(1));
        assertTrue(actual.getLabels().contains(Label.AUTH_REQUIRED));
        assertThat(actual.getErrorTargets().size(), is(1));
        assertThat(actual.getErrorTargets().get(StatusCode.NOT_FOUND).getResource(), is(notFoundResource));
        assertThat(actual.getValidate(), is(validate));
    }

    @Test
    public void buildWhenMethodContentTypeAndAcceptShouldBeOk() {
        RestTargetBuilder<DummySession, DummyUser, DummyPayload> subject = subject();

        OkRestResource notFoundResource = new OkRestResource();
        RestErrorTarget<DummySession, DummyUser, DummyPayload> notFound = new RestErrorTarget<>(
                DummyPayload.class, notFoundResource, new ArrayList<>(), new ArrayList<>()
        );

        OkRestResource okRestResource = new OkRestResource();

        MimeType json = new MimeTypeBuilder().json().build();
        MimeType jwt = new MimeTypeBuilder().jwt().build();

        RestTarget<DummySession, DummyUser, DummyPayload> actual = subject
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(Method.GET, json)
                .contentType(Method.POST, json)
                .accept(Method.GET, jwt)
                .restResource(okRestResource)
                .before(new DummyRestBetween<>())
                .before(new DummyRestBetween<>())
                .after(new DummyRestBetween<>())
                .after(new DummyRestBetween<>())
                .onDispatchError(StatusCode.NOT_FOUND, notFound)
                .build();


        assertThat(actual.getContentTypes().size(), is(2));
        assertThat(actual.getContentTypes().get(Method.GET).get(0), is(json));
        assertThat(actual.getContentTypes().get(Method.POST).get(0), is(json));

        assertThat(actual.getAccepts().size(), is(1));
        assertThat(actual.getAccepts().get(Method.GET).size(), is(1));
        assertThat(actual.getAccepts().get(Method.GET).get(0), is(jwt));
    }

    @Test
    public void buildCrudWhenSessionAndAuthenticateShouldAddLabels() {
        RestTargetBuilder<DummySession, DummyUser, DummyPayload> subject = subject();


        OkRestResource okRestResource = new OkRestResource();

        RestTarget<DummySession, DummyUser, DummyPayload> actual = subject
                .regex("/foo")
                .crud()
                .restResource(okRestResource)
                .session()
                .authenticate()
                .build();

        assertThat(actual, is(notNullValue()));

        assertThat(actual.getLabels().size(), is(2));
        assertThat(actual.getLabels().get(0), is(Label.SESSION_REQUIRED));
        assertThat(actual.getLabels().get(1), is(Label.AUTH_REQUIRED));
    }

    @Test
    public void buildCrudWhenCsrfAndAuthenticateShouldAddLabels() {
        RestTargetBuilder<DummySession, DummyUser, DummyPayload> subject = subject();


        OkRestResource okRestResource = new OkRestResource();

        RestTarget<DummySession, DummyUser, DummyPayload> actual = subject
                .regex("/foo")
                .crud()
                .restResource(okRestResource)
                .csrf()
                .authenticate()
                .build();

        assertThat(actual, is(notNullValue()));

        assertThat(actual.getLabels().size(), is(2));
        assertThat(actual.getLabels().get(0), is(Label.CSRF_PROTECT));
        assertThat(actual.getLabels().get(1), is(Label.AUTH_REQUIRED));
    }
}
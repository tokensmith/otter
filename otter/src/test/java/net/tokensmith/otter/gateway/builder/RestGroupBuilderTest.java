package net.tokensmith.otter.gateway.builder;


import helper.entity.*;
import helper.entity.model.DummyErrorPayload;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import org.junit.Test;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.entity.rest.RestGroup;
import net.tokensmith.otter.translatable.Translatable;

import java.util.ArrayList;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class RestGroupBuilderTest {

    @Test
    public void buildShouldHaveEmptyAuthBetweens() {

        RestGroup<DummySession, DummyUser> actual = new RestGroupBuilder<DummySession, DummyUser>()
                .name("API")
                .sessionClazz(DummySession.class)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getAuthOptional(), is(notNullValue()));
        assertThat(actual.getAuthOptional().isPresent(), is(false));
        assertThat(actual.getAuthRequired(), is(notNullValue()));
        assertThat(actual.getAuthRequired().isPresent(), is(false));
        assertThat(actual.getRestErrors(), is(notNullValue()));
        assertThat(actual.getRestErrors().size(), is(0));
        assertThat(actual.getSessionClazz(), is(notNullValue()));
    }

    @Test
    public void buildShouldHaveAuthBetweens() {

        DummyRestBetween<DummySession, DummyUser> authRequired = new DummyRestBetween<DummySession, DummyUser>();
        DummyRestBetween<DummySession, DummyUser> authOptional = new DummyRestBetween<DummySession, DummyUser>();

        RestGroup<DummySession, DummyUser> actual = new RestGroupBuilder<DummySession, DummyUser>()
                .name("API")
                .authRequired(authRequired)
                .authOptional(authOptional)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getAuthOptional(), is(notNullValue()));
        assertThat(actual.getAuthOptional().isPresent(), is(true));
        assertThat(actual.getAuthOptional().get(), is(authOptional));
        assertThat(actual.getAuthRequired(), is(notNullValue()));
        assertThat(actual.getAuthRequired().isPresent(), is(true));
        assertThat(actual.getAuthRequired().get(), is(authRequired));
        assertThat(actual.getRestErrors(), is(notNullValue()));
        assertThat(actual.getRestErrors().size(), is(0));
    }

    @Test
    public void buildShouldHaveRestErrors() {

        ClientErrorRestResource errorRestResource = new ClientErrorRestResource();
        RestGroup<DummySession, DummyUser> actual = new RestGroupBuilder<DummySession, DummyUser>()
                .name("API")
                .onError(StatusCode.BAD_REQUEST, errorRestResource, DummyErrorPayload.class)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getAuthOptional(), is(notNullValue()));
        assertThat(actual.getAuthOptional().isPresent(), is(false));
        assertThat(actual.getAuthRequired(), is(notNullValue()));
        assertThat(actual.getAuthRequired().isPresent(), is(false));
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
    public void buildShouldHaveDispatchErrors() {

        ClientErrorRestResource errorRestResource = new ClientErrorRestResource();

        OkRestResource notFoundResource = new OkRestResource();
        RestErrorTarget<DummySession, DummyUser, DummyPayload> dispatchError = new RestErrorTarget<>(
                DummyPayload.class, notFoundResource, new ArrayList<>(), new ArrayList<>()
        );

        RestGroup<DummySession, DummyUser> actual = new RestGroupBuilder<DummySession, DummyUser>()
                .name("API")
                .onError(StatusCode.BAD_REQUEST, errorRestResource, DummyErrorPayload.class)
                .onDispatchError(StatusCode.UNSUPPORTED_MEDIA_TYPE, dispatchError)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getAuthOptional(), is(notNullValue()));
        assertThat(actual.getAuthOptional().isPresent(), is(false));
        assertThat(actual.getAuthRequired(), is(notNullValue()));
        assertThat(actual.getAuthRequired().isPresent(), is(false));
        assertThat(actual.getRestErrors(), is(notNullValue()));
        assertThat(actual.getRestErrors().size(), is(1));

        assertThat(actual.getRestErrors().get(StatusCode.BAD_REQUEST), is(notNullValue()));

        Class<DummyErrorPayload> payload = to(actual.getRestErrors().get(StatusCode.BAD_REQUEST).getPayload());
        assertThat(payload, is(notNullValue()));

        assertThat(actual.getRestErrors().get(StatusCode.BAD_REQUEST).getRestResource(), is(errorRestResource));

        assertThat(actual.getDispatchErrors(), is(notNullValue()));
        assertThat(actual.getDispatchErrors().size(), is(1));
        assertThat(actual.getDispatchErrors().get(StatusCode.UNSUPPORTED_MEDIA_TYPE), is(dispatchError));
    }
}
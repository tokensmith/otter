package org.rootservices.otter.gateway.builder;


import helper.entity.*;
import org.junit.Test;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.ClientError;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.error.NotFoundResource;
import org.rootservices.otter.gateway.entity.rest.RestErrorTarget;
import org.rootservices.otter.gateway.entity.rest.RestGroup;
import org.rootservices.otter.translatable.Translatable;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class RestGroupBuilderTest {

    @Test
    public void buildShouldHaveEmptyAuthBetweens() {

        RestGroup<DummyUser> actual = new RestGroupBuilder<DummyUser>()
                .name("API")
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getAuthOptional(), is(notNullValue()));
        assertThat(actual.getAuthOptional().isPresent(), is(false));
        assertThat(actual.getAuthRequired(), is(notNullValue()));
        assertThat(actual.getAuthRequired().isPresent(), is(false));
        assertThat(actual.getRestErrors(), is(notNullValue()));
        assertThat(actual.getRestErrors().size(), is(0));
    }

    @Test
    public void buildShouldHaveAuthBetweens() {

        DummyRestBetween<DummyUser> authRequired = new DummyRestBetween<DummyUser>();
        DummyRestBetween<DummyUser> authOptional = new DummyRestBetween<DummyUser>();

        RestGroup<DummyUser> actual = new RestGroupBuilder<DummyUser>()
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
        RestGroup<DummyUser> actual = new RestGroupBuilder<DummyUser>()
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
        RestErrorTarget<DummyUser, DummyPayload> dispatchError = new RestErrorTarget<>(
                DummyPayload.class, notFoundResource, new ArrayList<>(), new ArrayList<>()
        );

        RestGroup<DummyUser> actual = new RestGroupBuilder<DummyUser>()
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
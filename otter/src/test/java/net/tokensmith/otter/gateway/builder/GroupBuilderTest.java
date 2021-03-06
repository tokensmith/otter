package net.tokensmith.otter.gateway.builder;

import helper.entity.DummyBetween;
import helper.entity.ServerErrorResource;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.gateway.entity.Group;
import net.tokensmith.otter.gateway.entity.Label;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class GroupBuilderTest {

    @Test
    public void buildShouldHaveEmptyBetweens() {

        Group<DummySession, DummyUser> actual = new GroupBuilder<DummySession, DummyUser>()
                .name("API")
                .sessionClazz(DummySession.class)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getSessionClazz(), is(notNullValue()));

        assertThat(actual.getLabelBefore(), is(notNullValue()));
        assertThat(actual.getLabelBefore().size(), is(0));
        assertThat(actual.getLabelAfter(), is(notNullValue()));
        assertThat(actual.getLabelAfter().size(), is(0));

        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(0));
    }

    @Test
    public void buildShouldHaveAuthBetweens() {

        DummyBetween<DummySession, DummyUser> authRequired = new DummyBetween<DummySession, DummyUser>();
        DummyBetween<DummySession, DummyUser> authOptional = new DummyBetween<DummySession, DummyUser>();

        Group<DummySession, DummyUser> actual = new GroupBuilder<DummySession, DummyUser>()
                .name("API")
                .sessionClazz(DummySession.class)
                .before(Label.AUTH_REQUIRED, authRequired)
                .before(Label.AUTH_OPTIONAL, authOptional)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getSessionClazz(), is(notNullValue()));

        assertThat(actual.getLabelBefore(), is(notNullValue()));
        assertThat(actual.getLabelBefore().size(), is(2));
        assertThat(actual.getLabelBefore().get(Label.AUTH_REQUIRED).size(), is(1));
        assertThat(actual.getLabelBefore().get(Label.AUTH_REQUIRED).get(0), is(authRequired));
        assertThat(actual.getLabelBefore().get(Label.AUTH_OPTIONAL).size(), is(1));
        assertThat(actual.getLabelBefore().get(Label.AUTH_OPTIONAL).get(0), is(authOptional));
        assertThat(actual.getLabelAfter(), is(notNullValue()));
        assertThat(actual.getLabelAfter().size(), is(0));

        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(0));
    }

    @Test
    public void buildShouldHaveBeforesAndAfters() {

        DummyBetween<DummySession, DummyUser> before = new DummyBetween<DummySession, DummyUser>();
        DummyBetween<DummySession, DummyUser> after = new DummyBetween<DummySession, DummyUser>();

        Group<DummySession, DummyUser> actual = new GroupBuilder<DummySession, DummyUser>()
                .name("API")
                .before(before)
                .after(after)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));

        assertThat(actual.getBefores(), is(notNullValue()));
        assertThat(actual.getBefores().size(), is(1));
        assertThat(actual.getBefores().get(0), is(before));

        assertThat(actual.getAfters(), is(notNullValue()));
        assertThat(actual.getAfters().size(), is(1));
        assertThat(actual.getAfters().get(0), is(after));
    }

    @Test
    public void buildShouldHaveErrorResources() {

        DummyBetween<DummySession, DummyUser> authRequired = new DummyBetween<DummySession, DummyUser>();
        DummyBetween<DummySession, DummyUser> authOptional = new DummyBetween<DummySession, DummyUser>();

        ServerErrorResource serverErrorResource = new ServerErrorResource();

        Group<DummySession, DummyUser> actual = new GroupBuilder<DummySession, DummyUser>()
                .name("API")
                .sessionClazz(DummySession.class)
                .onError(StatusCode.SERVER_ERROR, serverErrorResource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getSessionClazz(), is(notNullValue()));

        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(1));
        assertThat(actual.getErrorResources().get(StatusCode.SERVER_ERROR), is(serverErrorResource));
    }

    @Test
    public void buildShouldHaveDispatchErrors() {

        DummyBetween<DummySession, DummyUser> authRequired = new DummyBetween<DummySession, DummyUser>();
        DummyBetween<DummySession, DummyUser> authOptional = new DummyBetween<DummySession, DummyUser>();

        ErrorTarget<DummySession, DummyUser> dispatchError = new ErrorTargetBuilder<DummySession, DummyUser>()
                .build();

        ServerErrorResource serverErrorResource = new ServerErrorResource();

        Group<DummySession, DummyUser> actual = new GroupBuilder<DummySession, DummyUser>()
                .name("API")
                .sessionClazz(DummySession.class)
                .onError(StatusCode.SERVER_ERROR, serverErrorResource)
                .onDispatchError(StatusCode.UNSUPPORTED_MEDIA_TYPE, dispatchError)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("API"));
        assertThat(actual.getSessionClazz(), is(notNullValue()));

        assertThat(actual.getErrorResources(), is(notNullValue()));
        assertThat(actual.getErrorResources().size(), is(1));
        assertThat(actual.getErrorResources().get(StatusCode.SERVER_ERROR), is(serverErrorResource));

        assertThat(actual.getDispatchErrors(), is(notNullValue()));
        assertThat(actual.getDispatchErrors().size(), is(1));
        assertThat(actual.getDispatchErrors().get(StatusCode.UNSUPPORTED_MEDIA_TYPE), is(dispatchError));
    }
}
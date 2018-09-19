package org.rootservices.otter.gateway.translator;

import helper.FixtureFactory;
import helper.entity.DummyBetween;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.FakeResource;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.builder.TargetBuilder;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.factory.BetweenFactory;
import org.rootservices.otter.security.builder.entity.Betweens;

import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LocationTranslatorTest {
    private LocationTranslator<DummySession, DummyUser> subject;
    @Mock
    private BetweenFactory<DummySession, DummyUser> mockBetweenFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new LocationTranslator<DummySession, DummyUser>(mockBetweenFactory);
    }

    @Test
    public void toShouldBeOk() {
        Betweens<DummySession, DummyUser> betweens = FixtureFactory.makeBetweens();
        when(mockBetweenFactory.make(any(), any())).thenReturn(betweens);

        Target<DummySession, DummyUser> target = FixtureFactory.makeTarget();

        Map<Method, Location<DummySession, DummyUser>> actual =  subject.to(target);

        assertThat(actual.size(), is(2));

        // GET
        assertThat(actual.get(Method.GET).getRoute(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getRoute().getResource(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getRoute().getResource(), Is.is(target.getResource()));

        assertThat(actual.get(Method.GET).getRoute().getBefore().size(), is(3));
        // ordering of before.
        assertThat(actual.get(Method.GET).getRoute().getBefore().get(0), is(betweens.getBefore().get(0)));
        assertThat(actual.get(Method.GET).getRoute().getBefore().get(1), is(target.getBefore().get(0)));
        assertThat(actual.get(Method.GET).getRoute().getBefore().get(2), is(target.getBefore().get(1)));

        assertThat(actual.get(Method.GET).getRoute().getAfter().size(), is(3));

        // ordering of after.
        assertThat(actual.get(Method.GET).getRoute().getAfter().get(0), is(betweens.getAfter().get(0)));
        assertThat(actual.get(Method.GET).getRoute().getAfter().get(1), is(target.getAfter().get(0)));
        assertThat(actual.get(Method.GET).getRoute().getAfter().get(2), is(target.getAfter().get(1)));

        assertThat(actual.get(Method.GET).getPattern(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getContentTypes().size(), Is.is(1));

        assertThat(actual.get(Method.GET).getErrorRoutes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.GET).getErrorRoutes().size(), Is.is(1));


        // POST
        assertThat(actual.get(Method.POST).getRoute(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getRoute().getResource(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getRoute().getResource(), Is.is(target.getResource()));
        assertThat(actual.get(Method.POST).getRoute().getBefore().size(), is(3));

        // ordering of before.
        assertThat(actual.get(Method.POST).getRoute().getBefore().get(0), is(betweens.getBefore().get(0)));
        assertThat(actual.get(Method.POST).getRoute().getBefore().get(1), is(target.getBefore().get(0)));
        assertThat(actual.get(Method.POST).getRoute().getBefore().get(2), is(target.getBefore().get(1)));

        assertThat(actual.get(Method.POST).getRoute().getAfter().size(), is(3));

        // ordering of after.
        assertThat(actual.get(Method.POST).getRoute().getAfter().get(0), is(betweens.getAfter().get(0)));
        assertThat(actual.get(Method.POST).getRoute().getAfter().get(1), is(target.getAfter().get(0)));
        assertThat(actual.get(Method.POST).getRoute().getAfter().get(2), is(target.getAfter().get(1)));

        assertThat(actual.get(Method.POST).getPattern(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getContentTypes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getContentTypes().size(), Is.is(1));

        assertThat(actual.get(Method.POST).getErrorRoutes(), Is.is(notNullValue()));
        assertThat(actual.get(Method.POST).getErrorRoutes().size(), Is.is(1));

    }
}
package org.rootservices.otter.gateway;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.gateway.builder.GroupBuilder;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.router.Engine;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;


public class GatewayTest {
    private static OtterAppFactory otterAppFactory = new OtterAppFactory();

    public Gateway subject(Map<String, LocationTranslator> locationTranslatorCache) {
        // had trouble mocking responses from generic methods so this does not use mocks.
        Shape shape = FixtureFactory.makeShape("test-enc-key", "test-sign-key");
        return new Gateway(otterAppFactory.engine(), otterAppFactory.locationTranslatorFactory(shape), locationTranslatorCache);
    }

    @Test
    public void groupShouldCache() throws Exception {
        Group<DummySession, DummyUser> apiGroup = new GroupBuilder<DummySession, DummyUser>()
                .name("API")
                .sessionClazz(DummySession.class)
                .build();

        Map<String, LocationTranslator> cache = new HashMap<>();
        Gateway subject = subject(cache);

        subject.group(apiGroup);

        assertThat(cache.size(), is(1));
        assertThat(cache.get("API"), is(notNullValue()));
    }

}
package net.tokensmith.otter.gateway;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.gateway.config.TranslatorConfig;
import org.junit.Before;
import org.junit.Test;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.translator.LocationTranslator;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class LocationTranslatorFactoryTest {
    private Shape shape;
    private LocationTranslatorFactory subject;

    @Before
    public void setUp() throws Exception {
        shape = FixtureFactory.makeShape("test-enc-key", "test-sign-key");
        subject = new LocationTranslatorFactory(shape);
    }

    @Test
    public void shouldMakeLocationTranslator() throws Exception {
        OtterAppFactory otterAppFactory = new OtterAppFactory();

        TranslatorConfig<DummySession, DummyUser> config = new TranslatorConfig.Builder<DummySession, DummyUser>()
                .sessionClazz(DummySession.class)
                .before(new HashMap<>())
                .after(new HashMap<>())
                .errorResources(new HashMap<>())
                .dispatchErrors(new HashMap<>())
                .defaultDispatchErrors(new HashMap<>())
                .onHalts(otterAppFactory.defaultOnHalts(shape))
                .build();

        LocationTranslator<DummySession, DummyUser> actual = subject.make(config);

        assertThat(actual, is(notNullValue()));
    }
}
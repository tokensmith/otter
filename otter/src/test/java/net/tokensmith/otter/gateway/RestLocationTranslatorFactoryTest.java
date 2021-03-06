package net.tokensmith.otter.gateway;

import helper.FixtureFactory;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.gateway.config.RestTranslatorConfig;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.translator.RestLocationTranslator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class RestLocationTranslatorFactoryTest {
    private OtterAppFactory otterAppFactory;
    private Shape shape;
    private RestLocationTranslatorFactory subject;

    @Before
    public void setUp() throws Exception {
        otterAppFactory = new OtterAppFactory();
        shape = FixtureFactory.makeShape("test-enc-key", "test-sign-key");
        subject = new RestLocationTranslatorFactory(shape);
    }

    @Test
    public void makeShouldNotThrowNPE() {

        Validate validate = otterAppFactory.restValidate();

        RestTranslatorConfig<DummySession, DummyUser> config = new RestTranslatorConfig.Builder<DummySession, DummyUser>()
                .sessionClazz(DummySession.class)
                .labelBefore(new HashMap<>())
                .labelAfter(new HashMap<>())
                .befores(new ArrayList<>())
                .afters(new ArrayList<>())
                .restErrors(new HashMap<>())
                .defaultErrors(new HashMap<>())
                .dispatchErrors(new HashMap<>())
                .defaultDispatchErrors(new HashMap<>())
                .validate(validate)
                .onHalts(otterAppFactory.defaultRestOnHalts(shape))
                .build();

        RestLocationTranslator<DummySession, DummyUser, DummyPayload> actual = subject.make(config);

        assertThat(actual, is(notNullValue()));
    }
}